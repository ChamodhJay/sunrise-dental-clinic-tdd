# Sunrise Dental Clinic Appointment and Patient Management System

Java 17, Servlet 4, JSP/JSTL, JDBC, Maven, and MySQL 8 implementation of the
ICBT CIS6003 Advanced Programming assignment. The project keeps the supplied
Servlet MVC stack but replaces the unrelated Product CRUD starter domain.

## Implemented roles and workflows

- Receptionist: login, register patient/appointment, search by appointment
  number, calculate and print the patient bill.
- Dentist: login, view only assigned appointment/patient details, record the
  diagnosis and treatment notes, complete the appointment transactionally.
- Clinic manager: login, generate daily appointment and billing reports,
  maintain treatment prices/availability and the consultation fee schedule,
  and manage staff user accounts, password resets, and account status.
- All staff: role-aware dashboard, help and safe session exit.

The bill formula is deliberately limited to the assignment-approved rule:
`treatment price + consultation fee`.

## Prerequisites

- JDK 17
- Maven 3.8.1+
- MySQL 8
- A Servlet 4 container such as Apache Tomcat 9 (Tomcat 10 uses the incompatible
  `jakarta.servlet` namespace)

## Setup and run

1. Create and seed the database:

   ```text
   mysql -u root -p < database/schema.sql
   ```

   Re-run this script when upgrading an existing project database; it installs
   the stored procedures, function, and triggers without deleting clinic data.

2. Configure `SUNRISE_DB_USER` and `SUNRISE_DB_PASSWORD`. Override
   `SUNRISE_DB_URL` when the database is not on the default local address.
   The application deliberately has no built-in database credentials. See
   `database/README.md`.
3. Build and test:

   ```text
   mvn clean verify
   ```

4. Verify the advanced database features against MySQL:

   ```text
   mysql -u root -p --table < database/verify_stored_programs.sql
   ```

5. Deploy `target/sunrise-dental-clinic.war` to Tomcat 9.
6. Open `http://localhost:8080/sunrise-dental-clinic/` and use one of the
   demonstration accounts documented in `database/README.md`.

## Continuous integration

`.github/workflows/maven.yml` runs a clean Java 17 Maven test, SpotBugs analysis,
and WAR packaging build for every GitHub push and pull request. The workflow
does not replace the required authenticated MySQL and Tomcat browser smoke test.

## Architectural notes

- MVC controllers are Servlets; JSPs live under `WEB-INF` and cannot be invoked
  directly.
- Services enforce business rules and role authorization.
- JDBC DAOs use prepared/callable statements, per-operation connections, and
  try-with-resources. Named MySQL stored procedures own multi-table
  transactions for appointment registration, treatment completion, billing,
  treatment maintenance, consultation-fee replacement, and account management.
- Database constraints remain the final defense against double booking,
  duplicate treatment records, invalid state values, duplicate bill lines, and
  multiple active fee schedules.
- A deterministic SQL function implements the approved bill formula. Targeted
  triggers enforce cross-table staff-role and lifecycle rules when writes come
  from outside the web application. See `database/STORED_PROGRAMS.md`.
- Passwords use salted PBKDF2-SHA256. Login performs a dummy password check for
  unknown accounts, and bounded throttling locks an account/client pair for 30
  seconds after three failures without allowing unbounded in-memory state.
- A single security filter applies UTF-8, CSRF validation, live account-status
  refresh, server-side role checks and baseline browser security headers.
- The authenticated receptionist JSON endpoint
  `GET /api/appointments?number=APT-...` demonstrates the assignment's
  distributed/web-service criterion without widening the approved use-case scope.

## UML alignment

The implementation follows the finalized models in the parent assignment
workspace:

- `use_case_diagram_final_recommended.puml`
- `class_diagram_final_recommended.puml`
- `er_diagram_final_recommended.puml`

The account-management implementation is a new manager workflow added after the
finalized diagrams. Apply the localized updates in `UML_UPDATE_REQUIREMENTS.md`
before submitting the diagrams; no unrelated UML element needs redesigning.
Five sequence-diagram sources exist in the parent workspace, but they predate
the final Servlet implementation and must be corrected to use the actual
controller, DAO, endpoint, role, session, billing, and browser-print flows.
Activity and state diagrams are not required by the brief and are not present.

Technical sequence tables are omitted from the conceptual ER diagram because
they exist only to generate collision-free human-readable numbers.
