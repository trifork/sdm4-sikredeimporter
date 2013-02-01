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
package dk.nsi.sdm4.sikrede.recordspecs;

import dk.nsi.sdm4.core.persistence.recordpersister.RecordSpecification;

import static dk.nsi.sdm4.core.persistence.recordpersister.FieldSpecification.field;

public final class SikredeRecordSpecs
{
    protected SikredeRecordSpecs() {}
    
    public static final RecordSpecification START_RECORD_SPEC = RecordSpecification.createSpecification("DummyTable", "DummyKey", 
            field("PostType", 2, false).numerical().doNotPersist(),
            field("OpgDato", 8, false),
            field("Timestamp", 20, false),
            field("Modt", 6, false),
            field("SnitfladeId", 8, false));
    
    public static final RecordSpecification END_RECORD_SPEC = RecordSpecification.createSpecification("DummyTable", "DummyKey", 
            field("PostType", 2, false).numerical().doNotPersist(),
            field("AntPost", 8, false).numerical());
    
    public static final RecordSpecification ENTRY_RECORD_SPEC = RecordSpecification.createSpecification("Sikrede", "CPRnr", 
            // Page 1 (6 of 11)
            field("PostType", 2, false).numerical().doNotPersist(),
            field("CPRnr", 10, false),
            field("SYdernr", 6, false),
            field("SIkraftDatoYder", 8, false),
            field("SRegDatoYder", 8, false),
            field("SSikrGrpKode", 1, false),
            field("SIkraftDatoGrp", 8, false),
            field("SRegDatoGrp", 8, false),
            field("SSikrKomKode", 3, false),
            field("SIkraftDatoKomKode", 8, false),
            field("SYdernrGl", 6, false),
            field("SIkraftDatoYderGl", 8, false),
            field("SRegDatoYderGl", 8, false),
            field("SSikrGrpKodeGl", 1, false),
            field("SIkraftDatoGrpGl", 8, false),
            field("SRegDatoGrpGl", 8, false),
            field("SYdernrFrem", 6, false),
            field("SIkraftDatoYderFrem", 8, false),
            field("SRegDatoYderFrem", 8, false),
            field("SSikrGrpKodeFrem", 1, false),

            // Page 2 (7 of 11)
            field("SIkraftDatoGrpFrem", 8, false),
            field("SRegDatoGrpFrem", 8, false),
            field("SKon", 1, false),
            field("SAlder", 3, false),
            field("SFolgerskabsPerson", 10, false),
            field("SStatus", 2, false),
            field("SBevisDato", 8, false),
            // ...
            field("PNavn", 34, false),
            // ...
            field("SBSStatsborgerskabKode", 2, false),
            field("SBSStatsborgerskab", 47, false),
            field("SSKAdrLinie1", 40, false),
            field("SSKAdrLinie2", 40, false),

            // Page 3 (8 of 11)
            field("SSKBopelsLand", 40, false),
            field("SSKBopelsLAndKode", 2, false),
            field("SSKEmailAdr", 50, false),
            field("SSKFamilieRelation", 10, false),
            field("SSKFodselsdato", 10, false),
            field("SSKGyldigFra", 10, false),
            field("SSKGyldigTil", 10, false),
            field("SSKMobilNr", 20, false),
            field("SSKPostNrBy", 40, false),
            field("SSLForsikringsinstans", 21, false),
            field("SSLForsikringsinstansKode", 10, false),
            field("SSLForsikringsnr", 15, false),
            field("SSLGyldigFra", 10, false),
            field("SSLGyldigTil", 10, false),
            field("SSLSocSikretLand", 47, false),
            field("SSLSocSikretLandKode", 2, false));
}
