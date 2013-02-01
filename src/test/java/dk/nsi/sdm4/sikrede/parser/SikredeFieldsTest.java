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
import static dk.nsi.sdm4.core.persistence.recordpersister.FieldSpecification.RecordFieldType.NUMERICAL;
import static dk.nsi.sdm4.core.persistence.recordpersister.FieldSpecification.field;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import dk.nsi.sdm4.core.persistence.recordpersister.FieldSpecification;
import org.junit.Test;

import com.google.common.collect.Iterables;

import dk.nsi.sdm4.core.persistence.recordpersister.Record;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordSpecification;
import dk.nsi.sdm4.sikrede.recordspecs.SikredeRecordSpecs;

public class SikredeFieldsTest {
    @Test
    public void testCorrectNumberOfFields() {
        Iterable<FieldSpecification> fieldSpecs = SikredeRecordSpecs.ENTRY_RECORD_SPEC.getFieldSpecs();
        assertEquals(Iterables.size(fieldSpecs), 48);
    }

    @Test
    public void testCorrectNumberOfAlphanumericalFields() {
        Iterable<FieldSpecification> fieldSpecs = SikredeRecordSpecs.ENTRY_RECORD_SPEC.getFieldSpecs();

        int alphanumericalFields = 0;

        for (FieldSpecification spec : fieldSpecs) {
            if (spec.type == ALPHANUMERICAL) {
                alphanumericalFields++;
            }
        }

        assertEquals(47, alphanumericalFields);
    }

    @Test
    public void testCorrectNumberOfNumericalFields() {
        Iterable<FieldSpecification> fieldSpecs = SikredeRecordSpecs.ENTRY_RECORD_SPEC.getFieldSpecs();

        int numericalFields = 0;
        for (FieldSpecification spec : fieldSpecs) {
            if (spec.type == NUMERICAL) {
                numericalFields++;
            }
        }

        assertEquals(1, numericalFields);
    }

    @Test
    public void testCorrectAcceptedTotalLineLength() {
        RecordSpecification exampleRecordSpecification = RecordSpecification.createSpecification("SikredeGenerated",
                "Foo", field("Foo", 10, false).numerical(), field("Bar", 32, false));
        assertEquals(42, exampleRecordSpecification.acceptedTotalLineLength());
    }

    @Test
    public void testCorrectAcceptedTotalLineLengthForSingleton() {
        assertEquals(634, SikredeRecordSpecs.ENTRY_RECORD_SPEC.acceptedTotalLineLength());
    }

    @Test
    public void testConformsToSchemaSpecification() {
        RecordSpecification exampleRecordSpecification = RecordSpecification.createSpecification("SikredeGenerated",
                "Foo", field("Foo", 10, false).numerical(), field("Bar", 32, false));

        Record correctValues = RecordGenerator.createRecord("Foo", 42, "Bar", "12345678901234567890123456789012");
        Record correctValuesWhereBarIsShorter = RecordGenerator.createRecord("Foo", 42, "Bar",
                "123456789012345678901234567890");
        Record missingFoo = RecordGenerator.createRecord("Bar", "12345678901234567890123456789012");
        Record fooIsNotNumerical = RecordGenerator
                .createRecord("Foo", "Baz", "Bar", "12345678901234567890123456789012");
        Record barIsTooLong = RecordGenerator
                .createRecord("Foo", 42, "Bar", "1234567890123456789012345678901234567890");

        assertTrue(exampleRecordSpecification.conformsToSpecifications(correctValues));
        assertTrue(exampleRecordSpecification.conformsToSpecifications(correctValuesWhereBarIsShorter));
        assertFalse(exampleRecordSpecification.conformsToSpecifications(missingFoo));
        assertFalse(exampleRecordSpecification.conformsToSpecifications(fooIsNotNumerical));
        assertFalse(exampleRecordSpecification.conformsToSpecifications(barIsTooLong));
    }
}
