package com.icbt.dao;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DatabaseSchemaInvariantTest {
    private static String schema;

    @BeforeClass
    public static void loadSchema() throws IOException {
        schema = Files.readString(Path.of("database", "schema.sql"));
    }

    @Test
    public void generatedNumbersDoNotTruncateSequencesAfter9999() {
        assertFalse(schema.contains("LPAD(v_sequence, 4, '0')"));
        assertEquals(2, occurrences(
                "GREATEST(4, CHAR_LENGTH(CAST(v_sequence AS CHAR)))"));
        assertTrue(schema.contains("bill_number VARCHAR(40) NOT NULL UNIQUE"));
        assertTrue(schema.contains("OUT o_bill_number VARCHAR(40)"));
    }

    @Test
    public void billCreationLocksAppointmentBeforeCheckingForExistingBill() {
        int procedure = schema.indexOf("CREATE PROCEDURE sp_create_bill");
        int lock = schema.indexOf("SELECT appointment_id INTO v_locked_appointment_id", procedure);
        int existingBillCheck = schema.indexOf(
                "FROM bill WHERE appointment_id = p_appointment_id", procedure);

        assertTrue(procedure >= 0);
        assertTrue(lock > procedure);
        assertTrue(existingBillCheck > lock);
    }

    private int occurrences(String value) {
        int count = 0;
        int offset = 0;
        while ((offset = schema.indexOf(value, offset)) >= 0) {
            count++;
            offset += value.length();
        }
        return count;
    }
}
