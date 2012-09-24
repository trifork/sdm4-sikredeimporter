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

import static dk.nsi.sdm4.core.persistence.recordpersister.FieldSpecification.RecordFieldType.ALPHANUMERICAL;
import static dk.nsi.sdm4.core.persistence.recordpersister.FieldSpecification.RecordFieldType.DECIMAL10_3;
import static dk.nsi.sdm4.core.persistence.recordpersister.FieldSpecification.RecordFieldType.NUMERICAL;
import static org.junit.Assert.assertTrue;

import dk.nsi.sdm4.core.persistence.recordpersister.FieldSpecification;
import dk.nsi.sdm4.core.persistence.recordpersister.Record;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordSpecification;

public class RecordGenerator {
    private RecordSpecification recordSpecification;

    public RecordGenerator(RecordSpecification recordSpecification) {
        this.recordSpecification = recordSpecification;
    }

    public String stringFromRecords(Record record) {
        if (!recordSpecification.conformsToSpecifications(record)) {
            throw new IllegalArgumentException("Sikrede record does not conform to specification");
        }

        StringBuilder builder = new StringBuilder();
        for (FieldSpecification fieldSpecification : recordSpecification.getFieldSpecs()) {
	        try {
		        if (fieldSpecification.type == ALPHANUMERICAL) {
			        String value = (String) record.get(fieldSpecification.name);
			        builder.append(prefixPadding(' ', fieldSpecification.length - value.length()));
			        builder.append(value);
		        } else if (fieldSpecification.type == NUMERICAL) {
			        String value = Long.toString((Long) record.get(fieldSpecification.name));
			        builder.append(prefixPadding('0', fieldSpecification.length - value.length()));
			        builder.append(value);
		        } else {
			        throw new AssertionError("Missing implementation for type " + fieldSpecification.type);
		        }
	        } catch (RuntimeException e) {
		        throw new RuntimeException("While getting " + fieldSpecification.name + " from record " + record, e);
	        }
        }
        return builder.toString();
    }

    public String stringFromIncompleteRecord(Record record) {
        for (FieldSpecification fieldSpecification : recordSpecification.getFieldSpecs()) {
            if (!record.containsKey(fieldSpecification.name)) {
                if (fieldSpecification.type == ALPHANUMERICAL) {
                    record = record.put(fieldSpecification.name, "");
                } else if (fieldSpecification.type == NUMERICAL) {
                    record = record.put(fieldSpecification.name, 0L);
                } else if (fieldSpecification.type == DECIMAL10_3) {
	                record = record.put(fieldSpecification.name, 0.0);
                } else {
                    throw new AssertionError("Missing implementation for type " + fieldSpecification.type);
                }
            }
        }

        return stringFromRecords(record);
    }

    public String stringRecordFromIncompleteSetOfFields(Object... keysAndValues) {
        return stringFromIncompleteRecord(createRecord(keysAndValues));
    }

    public static Record createRecord(Object... keysAndValues) {
        Record record = new Record();

        assertTrue(keysAndValues.length % 2 == 0);

        for (int i = 0; i < keysAndValues.length; i += 2) {
            String key = (String) keysAndValues[i];
            Object value = keysAndValues[i + 1];
            record = record.put(key, value);
        }

        return record;
    }

    private String prefixPadding(char paddingElement, int n) {
        String padding = "";
        for (int i = 0; i < n; i++) {
            padding += paddingElement;
        }
        return padding;
    }
}
