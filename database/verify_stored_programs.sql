-- Read-only verification evidence for the advanced database implementation.
-- Run after schema.sql:
-- mysql -u root -p --table < database/verify_stored_programs.sql

USE sunrise_dental;

SELECT
  routine_type,
  routine_name,
  data_access,
  is_deterministic,
  security_type
FROM information_schema.routines
WHERE routine_schema = DATABASE()
  AND routine_name IN (
    'fn_calculate_bill_total',
    'sp_register_appointment',
    'sp_record_treatment',
    'sp_create_bill',
    'sp_save_treatment',
    'sp_replace_active_fee',
    'sp_create_staff_user',
    'sp_reset_staff_password',
    'sp_set_staff_active'
  )
ORDER BY routine_type, routine_name;

SELECT
  trigger_name,
  event_manipulation,
  event_object_table,
  action_timing
FROM information_schema.triggers
WHERE trigger_schema = DATABASE()
  AND trigger_name IN (
    'trg_dentist_role_before_insert',
    'trg_dentist_role_before_update',
    'trg_appointment_rules_before_insert',
    'trg_treatment_record_rules_before_insert',
    'trg_bill_rules_before_insert'
  )
ORDER BY event_object_table, trigger_name;

SELECT
  fn_calculate_bill_total(1500.00, 300.00) AS expected_total_1800_00,
  CASE
    WHEN fn_calculate_bill_total(1500.00, 300.00) = 1800.00 THEN 'PASS'
    ELSE 'FAIL'
  END AS bill_function_test;

SELECT
  (SELECT COUNT(*) FROM information_schema.routines
   WHERE routine_schema = DATABASE()
     AND routine_name IN (
       'fn_calculate_bill_total', 'sp_register_appointment',
       'sp_record_treatment', 'sp_create_bill',
       'sp_save_treatment', 'sp_replace_active_fee',
       'sp_create_staff_user', 'sp_reset_staff_password',
       'sp_set_staff_active'
     )) AS installed_routines,
  (SELECT COUNT(*) FROM information_schema.triggers
   WHERE trigger_schema = DATABASE()
     AND trigger_name IN (
       'trg_dentist_role_before_insert', 'trg_dentist_role_before_update',
       'trg_appointment_rules_before_insert',
       'trg_treatment_record_rules_before_insert',
       'trg_bill_rules_before_insert'
     )) AS installed_triggers;
