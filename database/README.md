# Database setup

1. Install MySQL 8 and start the server.
2. Run `mysql -u root -p < database/schema.sql` from the project directory.
   The script is rerunnable: tables and seed data are preserved while stored
   routines and triggers are replaced with their current definitions.
3. Configure the application with environment variables when the defaults are not suitable:
   - `SUNRISE_DB_URL`
   - `SUNRISE_DB_USER`
   - `SUNRISE_DB_PASSWORD`

The default URL is `jdbc:mysql://127.0.0.1:3306/sunrise_dental`. No password is
embedded in Java source. The blank default password exists only for common local
XAMPP classroom installations; use an environment variable for any other setup.

Demonstration accounts created by `schema.sql`:

| Role | Username | Initial password |
|---|---|---|
| Receptionist | `reception` | `Reception@123` |
| Dentist | `dentist` | `Dentist@123` |
| Clinic manager | `manager` | `Manager@123` |

These passwords are stored as salted PBKDF2-SHA256 hashes. Change the initial
passwords before using the system with real patient data.

## Stored-program verification

Run the read-only verification script after installing the schema:

```text
mysql -u root -p --table < database/verify_stored_programs.sql
```

Expected result: nine routines, five triggers, and a `PASS` calculation of
`1800.00`. See `STORED_PROGRAMS.md` for business-rule ownership and the
screenshots/evidence to include in the assignment report.
