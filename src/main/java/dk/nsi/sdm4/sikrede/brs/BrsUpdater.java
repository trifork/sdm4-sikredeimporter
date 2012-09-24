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

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.google.common.base.Preconditions;

import dk.nsi.sdm4.core.persistence.recordpersister.Record;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

public class BrsUpdater {
    static final long NO_EXISTING_RELATIONSHIP = -1;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public BrsUpdater() {
    }

    public void updateRecord(Record record) throws SQLException {
        String hashedCpr = hashCpr((String) record.get("CPRnr"));

        updateExistingRelationship(hashedCpr, (String) record.get("SYdernrGl"), parseSikredeRecordDate(record
                , "SIkraftDatoYderGl"), parseSikredeRecordDate(record, "SIkraftDatoYder"));
        insertRelationship(hashedCpr, (String) record.get("SYdernr"), parseSikredeRecordDate(record, "SIkraftDatoYder"), null);
    }

    void updateExistingRelationship(String patientCpr, String doctorOrganisationIdentifier, DateTime assignedFrom,
            DateTime assignedTo) throws SQLException {
        long primaryKeyFromExistingRelationship = openRelationshipExists(patientCpr, doctorOrganisationIdentifier);
        if (primaryKeyFromExistingRelationship == NO_EXISTING_RELATIONSHIP) {
            insertRelationship(patientCpr, doctorOrganisationIdentifier, assignedFrom, assignedTo);
        } else {
            closeRelationship(primaryKeyFromExistingRelationship, assignedTo);
        }
    }

    long openRelationshipExists(String patientCpr, String doctorOrganisationIdentifier) throws SQLException {
        String querySql = "SELECT pk FROM AssignedDoctor WHERE patientCpr = ? AND doctorOrganisationIdentifier = ? AND assignedTo IS NULL";
        Long result;
        try {
            result = jdbcTemplate.queryForLong(querySql, patientCpr, doctorOrganisationIdentifier);
        } catch (EmptyResultDataAccessException norelation) {
            result = NO_EXISTING_RELATIONSHIP;
        }

        return result;
    }

    void closeRelationship(long primaryKey, DateTime assignedTo) throws SQLException {
        String updateSql = "UPDATE AssignedDoctor SET assignedTo = ? WHERE pk = ?";
        jdbcTemplate.update(updateSql, new Date(assignedTo.getMillis()), primaryKey);
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

    static DateTime parseSikredeRecordDate(Record record, String key) {
	    String date = (String) record.get(key);
	    final int expectedLength = 8;
	    Preconditions.checkArgument(date.length() == expectedLength, "Date '%s' with key %s from record %s has wrong length, expected %s", date, key, record, expectedLength);
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
