# Sunrise Dental Clinic - Test Plan

## Purpose and approach

The test strategy combines fast automated unit/definition tests with a manual
MySQL/Tomcat workflow test. Unit tests verify deterministic validation, password
hashing, authorization, billing invariants, JSP bean compatibility, and the
presence of required database protections. Runtime tests must verify JDBC,
stored programs, sessions, role routing, HTML forms, and browser printing in the
deployed environment.

The current workspace has no Git history from which a test-first chronology can
be demonstrated. The automated tests are valid regression evidence, but they
must not be presented as proof that every feature was originally developed by
strict TDD. The report should explain the actual red-green-refactor examples
only where contemporaneous commit or screenshot evidence exists.

## Automated test data

| Area | Representative data | Expected result |
|---|---|---|
| Appointment validation | Future weekday at 09:30 with valid Sri Lankan phone | Accepted |
| Appointment validation | Weekend, 10:15 slot, malformed patient details | Field errors |
| Password hashing | `StrongPassword!` and an incorrect alternative | Correct password only verifies |
| Seed credentials | Receptionist, dentist, and manager PBKDF2 hashes | Documented initial passwords verify |
| Account validation | Valid dentist account and invalid/weak inputs | Valid accepted; invalid rejected |
| Authorization | Receptionist/dentist attempting manager operations | Security exception |
| Billing | Treatment 1500.00 plus consultation 300.00 | Total 1800.00 with two line types |
| Database definition | Stored routines, triggers, DAO calls, rerunnable seeds | Required definitions present |

## Automated execution

Run:

```text
mvn clean test
```

Verified on 13 August 2026: **21 tests passed, 0 failures, 0 errors, 0 skipped**.
The GitHub Actions workflow also runs `mvn clean verify` after the repository is
published.

## Required runtime test cases

| ID | Test | Expected result | Current status |
|---|---|---|---|
| RT-01 | Install `schema.sql`, then run `verify_stored_programs.sql` | 9 routines, 5 triggers, bill function PASS | Pending database credentials |
| RT-02 | Log in with each active demonstration account | Correct role dashboard opens | Pending |
| RT-03 | Submit invalid login three times | Generic error and 30-second throttle | Pending |
| RT-04 | Register a valid appointment | Unique number and full details displayed | Pending |
| RT-05 | Book the same dentist/date/time twice | Second request is rejected | Pending |
| RT-06 | Search an existing and unknown appointment number | Details, then clear not-found message | Pending |
| RT-07 | Dentist opens another dentist's appointment | Access rejected | Pending |
| RT-08 | Assigned dentist records treatment twice | First completes; duplicate rejected | Pending |
| RT-09 | Receptionist creates and prints a completed bill | Two lines and correct total; print dialog opens | Pending |
| RT-10 | Manager generates both reports | Correct rows or explicit empty state | Pending |
| RT-11 | Manager creates user, resets password, changes status | New login works; inactive login rejected | Pending |
| RT-12 | Receptionist/dentist requests `/users` directly | HTTP 403 | Pending |
| RT-13 | Submit a POST without a valid CSRF token | HTTP 403 | Pending |
| RT-14 | Log out, then revisit a protected URL | Session is invalid and login is required | Pending |

## Evidence required for the assignment report

- Screenshot of the Maven test summary.
- Screenshot of stored-program verification output.
- Screenshots of successful and rejected role-based workflows.
- Test-case table updated with actual result, date, tester, and evidence ID.
- Short evaluation of defects found, fixes applied, remaining limitations, and
  lessons learned.
