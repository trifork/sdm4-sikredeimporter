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
            field("PostType", 2).numerical().doNotPersist(),
            field("OpgDato", 8),
            field("Timestamp", 20),
            field("Modt", 6),
            field("SnitfladeId", 8));
    
    public static final RecordSpecification END_RECORD_SPEC = RecordSpecification.createSpecification("DummyTable", "DummyKey", 
            field("PostType", 2).numerical().doNotPersist(),
            field("AntPost", 8).numerical());
    
    public static final RecordSpecification ENTRY_RECORD_SPEC = RecordSpecification.createSpecification("Sikrede", "CPRnr", 
            // Page 1 (6 of 11)
            field("PostType", 2).numerical().doNotPersist(),
            field("CPRnr", 10),
            field("SYdernr", 6),
            field("SIkraftDatoYder", 8),
            field("SRegDatoYder", 8),
            field("SSikrGrpKode", 1),
            field("SIkraftDatoGrp", 8),
            field("SRegDatoGrp", 8),
            field("SSikrKomKode", 3),
            field("SIkraftDatoKomKode", 8),
            field("SYdernrGl", 6),
            field("SIkraftDatoYderGl", 8),
            field("SRegDatoYderGl", 8),
            field("SSikrGrpKodeGl", 1),
            field("SIkraftDatoGrpGl", 8),
            field("SRegDatoGrpGl", 8),
            field("SYdernrFrem", 6),
            field("SIkraftDatoYderFrem", 8),
            field("SRegDatoYderFrem", 8),
            field("SSikrGrpKodeFrem", 1),

            // Page 2 (7 of 11)
            field("SIkraftDatoGrpFrem", 8),
            field("SRegDatoGrpFrem", 8),
            field("SKon", 1),
            field("SAlder", 3),
            field("SFolgerskabsPerson", 10),
            field("SStatus", 2),
            field("SBevisDato", 8),
            // ...
            field("PNavn", 34),
            // ...
            field("SBSStatsborgerskabKode", 2),
            field("SBSStatsborgerskab", 47),
            field("SSKAdrLinie1", 40),
            field("SSKAdrLinie2", 40),

            // Page 3 (8 of 11)
            field("SSKBopelsLand", 40),
            field("SSKBopelsLAndKode", 2),
            field("SSKEmailAdr", 50),
            field("SSKFamilieRelation", 10),
            field("SSKFodselsdato", 10),
            field("SSKGyldigFra", 10),
            field("SSKGyldigTil", 10),
            field("SSKMobilNr", 20),
            field("SSKPostNrBy", 40),
            field("SSLForsikringsinstans", 21),
            field("SSLForsikringsinstansKode", 10),
            field("SSLForsikringsnr", 15),
            field("SSLGyldigFra", 10),
            field("SSLGyldigTil", 10),
            field("SSLSocSikretLand", 47),
            field("SSLSocSikretLandKode", 2));
}
