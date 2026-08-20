# Advanced database features

The assignment's Excellent 70-100 criterion for Design Patterns and
Architecture explicitly identifies appropriate stored procedures, functions,
and triggers that implement business rules. The system uses each feature only
where the database is the correct consistency boundary.

## Stored procedures

| Routine | Transactional responsibility | Business rules enforced |
|---|---|---|
| `sp_register_appointment` | Reuse/create patient, generate number, create appointment | Valid patient data, authorized active references, future weekday clinic slot, unique dentist slot |
| `sp_record_treatment` | Insert treatment record and complete appointment | Existing scheduled appointment, authenticated dentist is assigned dentist, one treatment record |
| `sp_create_bill` | Generate bill number, bill header and two lines | Receptionist role, completed treatment, one bill, one active fee, treatment plus consultation formula |
| `sp_save_treatment` | Insert or update catalogue entry | Clinic-manager role, unique name, non-negative price |
| `sp_replace_active_fee` | Deactivate old fee and activate new fee | Clinic-manager role, non-negative fee, exactly one active schedule |
| `sp_create_staff_user` | Create login and optional dentist profile | Clinic-manager role, unique username, valid role, PBKDF2 hash, atomic dentist profile |
| `sp_reset_staff_password` | Replace a stored password hash | Clinic-manager role, existing user, PBKDF2 hash only |
| `sp_set_staff_active` | Activate/deactivate login and dentist booking status | Clinic-manager role, no self/last-manager deactivation, protect scheduled dentist work |

Every multi-table procedure defines an `SQLEXCEPTION` handler that rolls back
and re-signals the original error. This prevents partially registered
appointments, treatment records without completion, incomplete bills, and a
clinic with no active consultation fee after a failed change.

Appointment and bill numbers retain four-digit zero-padding for small values
without truncating sequence values after 9,999. `sp_create_bill` locks the
appointment before its idempotency check so concurrent requests return the same
bill instead of racing into the unique constraint.

## Function

`fn_calculate_bill_total(treatmentPrice, consultationFee)` is deterministic and
has `NO SQL` data access. It rejects null/negative inputs and implements the
assignment formula without unsupported tax or discount logic.

## Triggers

The five `BEFORE INSERT/UPDATE` triggers protect cross-table invariants that
ordinary `CHECK` constraints cannot express:

- a dentist profile must use an active staff account with the DENTIST role;
- an appointment requires an active receptionist, dentist and treatment;
- a scheduled appointment must use a valid future clinic slot;
- a treatment record requires the assigned dentist and SCHEDULED state;
- a bill requires an active receptionist and a completed treatment record.

Triggers are not used for calculations or broad hidden side effects. Workflow
orchestration remains explicit in named stored procedures, which keeps the
database behavior understandable and testable.

## Java integration

`AppointmentDAO`, `BillDAO`, `ReferenceDataDAO`, and `StaffUserDAO` call the routines using
JDBC `CallableStatement`. The service layer performs early validation and role
checks for usable error messages, but database routines repeat critical checks
to prevent bypass by concurrent requests or another client.

This is defense in depth, not accidental duplication:

1. The UI guides input.
2. The service layer rejects invalid commands before database work.
3. Stored procedures control atomic workflow transitions.
4. Triggers, foreign keys, unique constraints and checks protect all write paths.

## Verification and report evidence

After running `schema.sql`, execute:

```text
mysql -u root -p --table < database/verify_stored_programs.sql
```

The expected evidence is nine installed routines, five installed triggers, and
`PASS` with `1800.00` for the bill-function test. Capture that terminal result
and the successful appointment-to-treatment-to-bill browser flow for the
assignment report. Do not claim runtime verification until these commands have
been run against the submission database.
