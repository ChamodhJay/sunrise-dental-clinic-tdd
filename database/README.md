# Database setup

1. Install MySQL 8 and start the server.
2. Run `mysql -u root -p < database/schema.sql` from the project directory.
   The script is rerunnable: tables and seed data are preserved while stored
   routines and triggers are replaced with their current definitions.
3. Configure the application credentials before starting Tomcat:
   - `SUNRISE_DB_USER` (required)
   - `SUNRISE_DB_PASSWORD` (required; use a restricted application account)
   - `SUNRISE_DB_URL` (optional)

The default URL targets `jdbc:mysql://127.0.0.1:3306/sunrise_dental`, uses the
driver's preferred TLS mode, and applies finite connection/socket timeouts. No
database username or password is embedded in Java source. Do not run the web
application as MySQL `root`; grant its account only the table and routine access
needed by this schema. A local classroom server with an intentionally blank
password must still set the password explicitly, for example with the JVM
property `-Dsunrise.db.password=`.

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
