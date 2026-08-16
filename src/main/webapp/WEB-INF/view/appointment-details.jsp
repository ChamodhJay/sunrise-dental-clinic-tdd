<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Appointment details · Sunrise Dental Clinic</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<%@ include file="fragments/header.jspf" %>
<main class="page">
  <div class="page-heading"><h1>Appointment details</h1><p><c:out value="${appointment.appointmentNumber}" /></p></div>
  <c:if test="${param.created eq '1'}"><div class="alert success no-print">Appointment registered successfully.</div></c:if>
  <section class="card">
    <dl class="details">
      <dt>Appointment number</dt><dd><strong><c:out value="${appointment.appointmentNumber}" /></strong></dd>
      <dt>Patient</dt><dd><c:out value="${appointment.patient.fullName}" /></dd>
      <dt>Contact</dt><dd><c:out value="${appointment.patient.contactNumber}" /></dd>
      <dt>Address</dt><dd><c:out value="${appointment.patient.address}" /></dd>
      <dt>Dentist</dt><dd><c:out value="${appointment.dentist.fullName}" /></dd>
      <dt>Treatment type</dt><dd><c:out value="${appointment.treatmentType.name}" /> (Rs. <fmt:formatNumber value="${appointment.treatmentType.basePrice}" minFractionDigits="2" />)</dd>
      <dt>Date and time</dt><dd><c:out value="${appointment.appointmentDate}" /> at <c:out value="${appointment.appointmentTime}" /></dd>
      <dt>Status</dt><dd><span class="status ${appointment.status}"><c:out value="${appointment.status}" /></span></dd>
      <dt>Registered by</dt><dd><c:out value="${appointment.registeredBy.fullName}" /></dd>
      <dt>Registered at</dt><dd><c:out value="${appointment.createdAt}" /></dd>
      <c:if test="${not empty appointment.treatmentRecord}">
        <dt>Diagnosis</dt><dd><c:out value="${appointment.treatmentRecord.diagnosis}" /></dd>
        <dt>Treatment notes</dt><dd><c:out value="${appointment.treatmentRecord.treatmentNotes}" /></dd>
        <dt>Completed at</dt><dd><c:out value="${appointment.treatmentRecord.completedAt}" /></dd>
      </c:if>
    </dl>
    <div class="button-row no-print top-gap">
      <button class="button" type="button" onclick="window.print()">Print details</button>
      <a class="button secondary" href="${pageContext.request.contextPath}/appointments">Search another</a>
      <c:if test="${appointment.status.name() eq 'COMPLETED'}">
        <a class="button secondary" href="${pageContext.request.contextPath}/billing?appointmentNumber=${appointment.appointmentNumber}">View bill</a>
      </c:if>
    </div>
  </section>
</main>
<%@ include file="fragments/footer.jspf" %>
</body>
</html>
