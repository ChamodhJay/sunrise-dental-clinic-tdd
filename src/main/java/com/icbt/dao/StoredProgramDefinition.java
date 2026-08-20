package com.icbt.dao;

public final class StoredProgramDefinition {
    public static final String REGISTER_APPOINTMENT = "{CALL sp_register_appointment(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
    public static final String RECORD_TREATMENT = "{CALL sp_record_treatment(?, ?, ?, ?, ?)}";
    public static final String SAVE_TREATMENT = "{CALL sp_save_treatment(?, ?, ?, ?, ?)}";
    public static final String REPLACE_ACTIVE_FEE = "{CALL sp_replace_active_fee(?, ?, ?, ?)}";
    public static final String CREATE_BILL = "{CALL sp_create_bill(?, ?, ?, ?)}";
    public static final String CREATE_STAFF_USER = "{CALL sp_create_staff_user(?, ?, ?, ?, ?, ?, ?)}";
    public static final String RESET_STAFF_PASSWORD = "{CALL sp_reset_staff_password(?, ?, ?)}";
    public static final String SET_STAFF_ACTIVE = "{CALL sp_set_staff_active(?, ?, ?)}";

    private StoredProgramDefinition() { }
}
