package com.icbt.dao;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Validates the syntax constraints and matching literals for JDBC
 * CallableStatements across the sunrise dental system.
 */
public class StoredProgramDefinitionTest {

    @Test
    public void registerAppointmentDefinitionIsCorrect() {
        assertEquals("{CALL sp_register_appointment(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}", 
                     StoredProgramDefinition.REGISTER_APPOINTMENT);
    }

    @Test
    public void recordTreatmentDefinitionIsCorrect() {
        assertEquals("{CALL sp_record_treatment(?, ?, ?, ?, ?)}", 
                     StoredProgramDefinition.RECORD_TREATMENT);
    }

    @Test
    public void saveTreatmentDefinitionIsCorrect() {
        assertEquals("{CALL sp_save_treatment(?, ?, ?, ?, ?)}", 
                     StoredProgramDefinition.SAVE_TREATMENT);
    }

    @Test
    public void replaceActiveFeeDefinitionIsCorrect() {
        assertEquals("{CALL sp_replace_active_fee(?, ?, ?, ?)}", 
                     StoredProgramDefinition.REPLACE_ACTIVE_FEE);
    }

    @Test
    public void createBillDefinitionIsCorrect() {
        assertEquals("{CALL sp_create_bill(?, ?, ?, ?)}", 
                     StoredProgramDefinition.CREATE_BILL);
    }

    @Test
    public void createStaffUserDefinitionIsCorrect() {
        assertEquals("{CALL sp_create_staff_user(?, ?, ?, ?, ?, ?, ?)}", 
                     StoredProgramDefinition.CREATE_STAFF_USER);
    }

    @Test
    public void resetStaffPasswordDefinitionIsCorrect() {
        assertEquals("{CALL sp_reset_staff_password(?, ?, ?)}", 
                     StoredProgramDefinition.RESET_STAFF_PASSWORD);
    }

    @Test
    public void setStaffActiveDefinitionIsCorrect() {
        assertEquals("{CALL sp_set_staff_active(?, ?, ?)}", 
                     StoredProgramDefinition.SET_STAFF_ACTIVE);
    }

    @Test
    public void parameterCountsMatchPlaceholders() {
        assertTrue(StoredProgramDefinition.REGISTER_APPOINTMENT.chars().filter(ch -> ch == '?').count() == 14);
        assertTrue(StoredProgramDefinition.RECORD_TREATMENT.chars().filter(ch -> ch == '?').count() == 5);
        assertTrue(StoredProgramDefinition.CREATE_BILL.chars().filter(ch -> ch == '?').count() == 4);
        assertTrue(StoredProgramDefinition.SAVE_TREATMENT.chars().filter(ch -> ch == '?').count() == 5);
        assertTrue(StoredProgramDefinition.REPLACE_ACTIVE_FEE.chars().filter(ch -> ch == '?').count() == 4);
        assertTrue(StoredProgramDefinition.CREATE_STAFF_USER.chars().filter(ch -> ch == '?').count() == 7);
        assertTrue(StoredProgramDefinition.RESET_STAFF_PASSWORD.chars().filter(ch -> ch == '?').count() == 3);
        assertTrue(StoredProgramDefinition.SET_STAFF_ACTIVE.chars().filter(ch -> ch == '?').count() == 3);
    }
}
