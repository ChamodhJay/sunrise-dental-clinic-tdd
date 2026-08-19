<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Help · Sunrise Dental Clinic</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<%@ include file="fragments/header.jspf" %>
<main class="page">
  <div class="page-heading"><h1>Help instructions</h1><p>Guidance for <c:out value="${sessionScope.authUser.role}" /> users.</p></div>
  <section class="card">
    <h2>Common guidance</h2>
    <ol>
      <li>Never share your username or password, and use Exit when your work is complete.</li>
      <li>Appointment numbers are the official lookup key. Copy them exactly from the confirmation.</li>
      <li>If a form reports an error, correct the identified field; valid input is preserved.</li>
      <li>Use the browser print dialog for confirmations, details, receipts, and reports.</li>
    </ol>
  </section>
  <c:if test="${sessionScope.authUser.role.name() eq 'RECEPTIONIST'}"><section class="card">
    <h2>Receptionist workflow</h2><ol>
      <li>Register the patient and select an active dentist, treatment, future weekday, and 30-minute clinic slot.</li>
      <li>If the patient contact number already exists, the patient record is reused and its current name/address are updated.</li>
      <li>Search using the generated appointment number. Billing becomes available only after the assigned dentist completes treatment.</li>
      <li>A bill always contains exactly two charges: treatment price and the active consultation fee.</li>
    </ol>
  </section></c:if>
  <c:if test="${sessionScope.authUser.role.name() eq 'DENTIST'}"><section class="card">
    <h2>Dentist workflow</h2><ol>
      <li>Open My appointments to see only records assigned to your dentist identity.</li>
      <li>Select a scheduled appointment, record both diagnosis and treatment notes, and submit once.</li>
      <li>The treatment record and COMPLETED status are saved in one transaction and cannot be duplicated.</li>
    </ol>
  </section></c:if>
  <c:if test="${sessionScope.authUser.role.name() eq 'CLINIC_MANAGER'}"><section class="card">
    <h2>Clinic manager workflow</h2><ol>
      <li>Generate the daily appointment report for a date or billing summary for a range up to 366 days.</li>
      <li>Use User accounts to create staff logins, reset passwords, and activate or deactivate accounts. Dentist accounts receive a linked booking profile automatically.</li>
      <li>Update treatment prices or deactivate treatments that must not appear in new bookings.</li>
      <li>Activating a new consultation fee closes the prior fee schedule while existing bills retain their original fee reference.</li>
    </ol>
  </section></c:if>
</main>
<%@ include file="fragments/footer.jspf" %>
</body>
</html>
