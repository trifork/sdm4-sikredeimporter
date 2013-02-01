/**
 * The MIT License
 *
 * Original work sponsored and donated by National Board of e-Health (NSI), Denmark
 * (http://www.nsi.dk)
 *
 * Copyright (C) 2011 National Board of e-Health (NSI), Denmark (http://www.nsi.dk)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dk.nsi.sdm4.sikrede.parser;

import dk.nsi.sdm4.core.parser.ParserException;
import dk.nsi.sdm4.core.persistence.recordpersister.Record;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordSpecification;
import dk.nsi.sdm4.sikrede.config.SikredeimporterApplicationConfig;
import dk.nsi.sdm4.sikrede.recordspecs.SikredeRecordSpecs;
import dk.nsi.sdm4.testutils.TestDbConfiguration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = {SikredeimporterApplicationConfig.class, TestDbConfiguration.class})
public class SikredeParserTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SikredeParser parser;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

	RecordSpecification recordSpecification = SikredeRecordSpecs.ENTRY_RECORD_SPEC;

    @Test
    public void testEmptyFile() throws Exception {
        File input = setupExampleFile();

        parser.process(input);
        
        assertEquals(0, jdbcTemplate.queryForInt("SELECT Count(*) FROM " + recordSpecification.getTable()));
    }

	@Test
	public void testAbleToInsertFourRecords() throws Exception {
		Object[] keysAndValues = {"PostType", 10L, "CPRnr", "1234567890", "SIkraftDatoYderGl", "19990101", "SIkraftDatoYder", "19990101"};
		File inbox = setupExampleFile(
				RecordGenerator.createRecord(keysAndValues),
				RecordGenerator.createRecord(keysAndValues),
				RecordGenerator.createRecord(keysAndValues),
				RecordGenerator.createRecord(keysAndValues));

				parser.process(inbox);

		assertNumberOfSikredeGeneratedRecordsInDatabaseIs(4, recordSpecification);
	}

	@Test(expected = ParserException.class)
	public void testIllegalStartRecord() throws Exception {
		Object[] keysAndValues = {"PostType", 10L, "CPRnr", "1234567890", "SIkraftDatoYderGl", "19990101", "SIkraftDatoYder", "19990101"};
		File inbox = setupExampleFileWithIllegalReceiverId(
				RecordGenerator.createRecord(keysAndValues),
				RecordGenerator.createRecord(keysAndValues),
				RecordGenerator.createRecord(keysAndValues),
				RecordGenerator.createRecord(keysAndValues)
		);

		parser.process(inbox);
	}

    @Test
    public void testSameDoctorTwice() throws IOException {
        Object[] keysAndValues1 = {"PostType", 10L, "CPRnr", "1234567890", "SIkraftDatoYderGl", "00000000",
                "SIkraftDatoYder", "19990101", "SYdernrGl", "000000", "SYdernr", "112233"};
        Object[] keysAndValues2 = {"PostType", 10L, "CPRnr", "1234567890", "SIkraftDatoYderGl", "00000000",
                "SIkraftDatoYder", "20000101", "SYdernrGl", "000000", "SYdernr", "112244"};
        File inbox = setupExampleFile(
                RecordGenerator.createRecord(keysAndValues1),
                RecordGenerator.createRecord(keysAndValues2),
                RecordGenerator.createRecord(keysAndValues1)
        );

        parser.process(inbox);
    }

    @Test
    public void testNewFileFormatWithInvalidDates() throws IOException {
        File inbox = setupRealUdtraekTest();
        parser.process(inbox);
    }

    private File setupRealUdtraekTest() throws IOException {
        InputStream udtraekStream = SikredeParser.class.getClassLoader().getResourceAsStream("sikrede-udtraek-filtered.txt");
        byte[] data = new byte[udtraekStream.available()];
        udtraekStream.read(data);

        File inbox =temporaryFolder.newFolder("udtraek");
        File file = temporaryFolder.newFile("udtraek/sikrede-udtraek-filtered.txt");

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(data);
        fileOutputStream.flush();
        fileOutputStream.close();

        return inbox;
    }

    private File setupExampleFile(Record... records) throws IOException {
        RecordGenerator startGenerator = new RecordGenerator(SikredeRecordSpecs.START_RECORD_SPEC);
        RecordGenerator endGenerator = new RecordGenerator(SikredeRecordSpecs.END_RECORD_SPEC);

        StringBuilder builder = new StringBuilder();

        builder.append(startGenerator.stringRecordFromIncompleteSetOfFields("PostType", 0L, "Modt", "F053",
                "SnitfladeId", "S1061023"));
        builder.append('\n');

        RecordGenerator entryGenerator = new RecordGenerator(recordSpecification);

        for (Record record : records) {
            builder.append(entryGenerator.stringFromIncompleteRecord(record));
            builder.append('\n');
        }

        builder.append(endGenerator.stringRecordFromIncompleteSetOfFields("PostType", 99L, "AntPost", new Long(records.length)));
        builder.append('\n');

        File inbox = temporaryFolder.newFolder("foo");
        File file = temporaryFolder.newFile("foo/foo.txt");

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, SikredeParser.FILE_ENCODING);
        outputStreamWriter.write(builder.toString());
        outputStreamWriter.flush();
        fileOutputStream.close();

        return inbox;
    }

    private File setupExampleFileWithIllegalReceiverId(Record... records)
            throws IOException {
        RecordGenerator startGenerator = new RecordGenerator(SikredeRecordSpecs.START_RECORD_SPEC);
        RecordGenerator endGenerator = new RecordGenerator(SikredeRecordSpecs.END_RECORD_SPEC);

        StringBuilder builder = new StringBuilder();

        builder.append(startGenerator.stringRecordFromIncompleteSetOfFields("PostType", 0L, "Modt", "F042",
                "SnitfladeId", "S1061023"));
        builder.append('\n');

        RecordGenerator entryGenerator = new RecordGenerator(recordSpecification);

        for (Record record : records) {
            builder.append(entryGenerator.stringFromIncompleteRecord(record));
            builder.append('\n');
        }

        builder.append(endGenerator.stringRecordFromIncompleteSetOfFields("PostType", 99L, "AntPost", new Long(records.length)));
        builder.append('\n');

        File inbox = temporaryFolder.newFolder("foo");
        File file = temporaryFolder.newFile("foo/foo.txt");

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, SikredeParser.FILE_ENCODING);
        outputStreamWriter.write(builder.toString());
        outputStreamWriter.flush();
        fileOutputStream.close();

        return inbox;
    }

	private void assertNumberOfSikredeGeneratedRecordsInDatabaseIs(int i, RecordSpecification spec) throws SQLException
	{
		assertEquals(i, jdbcTemplate.queryForInt("SELECT Count(*) FROM " + spec.getTable()));
	}
}
