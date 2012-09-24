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

import com.google.common.base.Preconditions;
import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.parser.ParserException;
import dk.nsi.sdm4.core.parser.SingleLineRecordParser;
import dk.nsi.sdm4.core.persistence.recordpersister.Record;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordPersister;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordSpecification;
import dk.nsi.sdm4.sikrede.brs.BrsUpdater;
import dk.nsi.sdm4.sikrede.recordspecs.SikredeRecordSpecs;
import dk.sdsd.nsp.slalog.api.SLALogItem;
import dk.sdsd.nsp.slalog.api.SLALogger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.Iterator;

import static java.lang.String.format;

public class SikredeParser implements Parser {
	// default settings, is overwritten by configuration
    
    @Value("${spooler.sikrede.recipientid}")
    private String acceptedRecipientId = "F053";
    @Value("${spooler.sikrede.interfaceid}")
    private String acceptedInterfaceId = "S1061023";
    
    public static final String FILE_ENCODING = "ISO-8859-1";

    private static final String RECORD_TYPE_START = "00";
    private static final String RECORD_TYPE_ENTRY = "10";
    private static final String RECORD_TYPE_END = "99";

    private final SingleLineRecordParser recordParser;
    private final RecordSpecification recordSpecification;

    @Autowired
    private SLALogger slaLogger;
    
    @Autowired
    private RecordPersister persister;

    @Autowired
    private BrsUpdater brsUpdater;

    public SikredeParser() {
        recordSpecification = SikredeRecordSpecs.ENTRY_RECORD_SPEC;
        recordParser = new SingleLineRecordParser(recordSpecification);
    }

    @Override
    public void process(File dataSet) throws ParserException {
        SLALogItem slaLogItem = slaLogger.createLogItem("SikredeParser", "dataSet");
        try {
            File[] input = dataSet.listFiles();
            Preconditions.checkArgument(input.length == 1, "Only one file is expected at this point.");
            File file = input[0];

            MDC.put("filename", file.getName());

            // FIXME: Check that files are imported in the right order.
            // We can not do this yet as we do not know what the files are named.

            LineIterator lines = null;

            try {
                lines = FileUtils.lineIterator(file, FILE_ENCODING);
                importFile(lines, persister);
            } finally {
                LineIterator.closeQuietly(lines);
            }

            slaLogItem.setCallResultOk();
            slaLogItem.store();
        } catch (Exception e) {
            slaLogItem.setCallResultError("SikredeParser Failed - Cause: " + e.getMessage());
            slaLogItem.store();

            throw new ParserException(e);
        }
    }

    private void importFile(Iterator<String> lines, RecordPersister persister) throws Exception {
        // A set containing all CPR numbers that have changed.
        //
        Record startRecord = null;
        Record endRecord = null;

        long numRecords = 0;

        SingleLineRecordParser startRecordParser = new SingleLineRecordParser(SikredeRecordSpecs.START_RECORD_SPEC);
        SingleLineRecordParser endRecordParser = new SingleLineRecordParser(SikredeRecordSpecs.END_RECORD_SPEC);

        for (String line; lines.hasNext(); ) {
            if (endRecord != null) {
                throw new ParserException("Lines found after End Record.");
            }

            line = lines.next();

            if (line.startsWith(RECORD_TYPE_START)) {
                if (startRecord != null) {
                    throw new ParserException("Several Start Records were found.");
                }

                startRecord = startRecordParser.parseLine(line);

                // FIXME: Verificer Modt og SnitfladeId jf. dokumentation.
                //
                if (!acceptedRecipientId.equals(startRecord.get("Modt"))) {
                    throw new ParserException(format("The receiver id '%s' did not match the expected '%s'.", startRecord.get("Modt"), acceptedRecipientId));
                }

                if (!acceptedInterfaceId.equals(startRecord.get("SnitfladeId"))) {
                    throw new ParserException(format("The interface id '%s' did not match the expected '%s'.", startRecord.get("SnitfladeId"), acceptedInterfaceId));
                }
            } else if (line.startsWith(RECORD_TYPE_END)) {
                if (startRecord == null) throw new ParserException("Start record was not found before end record.");

                endRecord = endRecordParser.parseLine(line);
            } else if (line.startsWith(RECORD_TYPE_ENTRY)) {
                if (startRecord == null) throw new ParserException("Start record was not found before first entry.");

                Record record = recordParser.parseLine(line);

                brsUpdater.updateRecord(record);

                persister.persist(record, recordSpecification);

                numRecords++;
            } else {
                throw new ParserException("Unknown record type. line_content='" + line + "'");
            }
        }

        if (!endRecord.get("AntPost").equals(numRecords)) {
            throw new ParserException("The number of records that were parsed did not match the total from the end record.");
        }
    }

    @Override
    public String getHome() {
        return "sikredeimporter";
    }

}
