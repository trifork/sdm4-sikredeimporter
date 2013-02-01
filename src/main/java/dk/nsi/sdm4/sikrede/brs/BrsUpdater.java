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
package dk.nsi.sdm4.sikrede.brs;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.common.base.Preconditions;

import dk.nsi.sdm4.core.persistence.recordpersister.Record;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class BrsUpdater {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Logger log = Logger.getLogger(BrsUpdater.class);

    public BrsUpdater() {
    }

    public void updateRecord(Record record) throws SQLException {
        String hashedCpr = hashCpr((String) record.get("CPRnr"));

        updateExistingRelationship(hashedCpr,
                (String) record.get("SYdernrGl"),
                // The input was changed so we can now get a SIkraftDatoYderGl containing all zeroes, this indicates
                // that the person always had the same doctor, so we return epoch as nulls is not allowed in db.
                parseSikredeRecordDateDefaultDateIfEmpty(record, "SIkraftDatoYderGl"),
                parseSikredeRecordDate(record, "SIkraftDatoYder"));
        insertRelationship(hashedCpr,
                (String) record.get("SYdernr"),
                parseSikredeRecordDate(record, "SIkraftDatoYder"),
                null);
    }

    void updateExistingRelationship(String patientCpr,
                                    String doctorOrganisationIdentifier,
                                    DateTime assignedFrom,
                                    DateTime assignedTo) throws SQLException {
        List<Long> existingRelations = openRelationshipsExists(patientCpr, doctorOrganisationIdentifier);
        if (existingRelations.size() > 0) {
            log.debug("Found " + existingRelations.size() + " existing relationsships");
        }
        if (existingRelations == null || existingRelations.size() == 0) {
            insertRelationship(patientCpr, doctorOrganisationIdentifier, assignedFrom, assignedTo);
        } else {
            closeRelationships(existingRelations, assignedTo);
        }
    }

    List<Long> openRelationshipsExists(String patientCpr, String doctorOrganisationIdentifier) throws SQLException {
        String querySql = "SELECT pk FROM AssignedDoctor WHERE patientCpr = ? AND doctorOrganisationIdentifier = ? AND assignedTo IS NULL";
        try {
            return jdbcTemplate.queryForList(querySql, Long.class, patientCpr, doctorOrganisationIdentifier);
        } catch (EmptyResultDataAccessException norelation) {
            return null;
        }
    }

    void closeRelationships(List<Long> keys, DateTime assignedTo) throws SQLException {
        String updateSql = "UPDATE AssignedDoctor SET assignedTo = ? WHERE pk = ?";
        for (Long key : keys) {
            jdbcTemplate.update(updateSql, new Date(assignedTo.getMillis()), key);
        }
    }

    void insertRelationship(String patientCpr, String doctorOrganisationIdentifier, DateTime assignedFrom,
            DateTime assignedTo) throws SQLException {
        String insertSql = "INSERT INTO AssignedDoctor (patientCpr, doctorOrganisationIdentifier, assignedFrom, assignedTo, reference) VALUES (?, ?, ?, ?, ?)";

        Date assignedToDate = null;
        if (assignedTo != null) {
            assignedToDate = new Date(assignedTo.getMillis());
        }

        jdbcTemplate.update(insertSql, patientCpr, doctorOrganisationIdentifier, new Date(assignedFrom.getMillis()),
                assignedToDate, new DateTime().toString());
    }

    static String hashCpr(String cpr) {
        Preconditions.checkArgument(cpr.length() == 10);
        try {
            return hash(cpr);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parse a date, in case the input is all zeroes, epoch is returned.
     * @param record
     * @param key name of the key in the records that contains the date
     * @return
     */
    static DateTime parseSikredeRecordDateDefaultDateIfEmpty(Record record, String key) {
        String date = (String) record.get(key);
        if (date.equals("00000000")) {
            return new DateTime(0l);
        } else {
            return parseSikredeRecordDate(record, key);
        }
    }

    static DateTime parseSikredeRecordDate(Record record, String key) {
	    String date = (String) record.get(key);
	    final int expectedLength = 8;
	    Preconditions.checkArgument(date.length() == expectedLength,
                "Date '%s' with key %s from record %s has wrong length, expected %s", date, key, record, expectedLength);
        return new DateTime(
		        Integer.parseInt(date.substring(0, 4)),
		        Integer.parseInt(date.substring(4, 6)),
		        Integer.parseInt(date.substring(6, 8)),
		        0, 0, 0);
    }

    private static final String SHA_1 = "SHA-1";

    private static String hash(String string) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(SHA_1);
        digest.reset();
        byte[] bytes = digest.digest(string.getBytes());
        return getHex(bytes);
    }

    private static final String HEXES = "0123456789ABCDEF";

    private static String getHex(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

}
