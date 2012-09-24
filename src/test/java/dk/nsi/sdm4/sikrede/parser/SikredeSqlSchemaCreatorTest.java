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

import static dk.nsi.sdm4.core.persistence.recordpersister.FieldSpecification.field;
import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import dk.nsi.sdm4.core.persistence.recordpersister.RecordMySQLTableGenerator;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordSpecification;
import dk.nsi.sdm4.sikrede.recordspecs.SikredeRecordSpecs;

public class SikredeSqlSchemaCreatorTest {
	private static final Logger log = Logger.getLogger(SikredeSqlSchemaCreatorTest.class);
    private RecordSpecification exampleRecordSpecification;

    @Before
    public void createExampleSikredeFields() {
        exampleRecordSpecification = RecordSpecification.createSpecification("SikredeGenerated", "Foo",
                field("Foo", 10), field("Bar", 5).numerical(), field("Baz", 42));
    }

    @Test
    public void testExampleSikredeFields() {
        String expected = "CREATE TABLE SikredeGenerated (" + "PID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,"
                + "Foo VARCHAR(10)," + "Bar BIGINT," + "Baz VARCHAR(42)," + "ValidFrom DateTime NOT NULL,"
                + "ValidTo DateTime," + "ModifiedDate DateTime NOT NULL" + ") ENGINE=InnoDB COLLATE=utf8_bin;";

        String result = RecordMySQLTableGenerator.createSqlSchema(exampleRecordSpecification);

        assertEquals(expected, result.replaceAll("\n", "").replaceAll("\t", ""));
    }

    @Test
    public void testPrintOfActualSchema() {
        log.debug(RecordMySQLTableGenerator.createSqlSchema(SikredeRecordSpecs.ENTRY_RECORD_SPEC));
    }
}
