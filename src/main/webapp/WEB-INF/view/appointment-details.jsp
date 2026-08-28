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
  <div class="page-topbar no-print">
    <div class="breadcrumb">
      <span>Sunrise Dental Clinic</span>
      <span class="bc-sep">›</span>
      <span class="bc-current">Appointment Details</span>
    </div>
  </div>

  <div class="page-body">
    <div class="page-heading">
      <div class="focus-label">Clinic Workspace</div>
      <h1>Appointment details</h1>
      <p><c:out value="${appointment.appointmentNumber}" /></p>
    </div>

    <c:if test="${param.created eq '1'}"><div class="alert success no-print">Appointment registered successfully.</div></c:if>

    <section class="card">
      <dl class="details">
        <dt>Appointment number</dt><dd><strong><c:out value="${appointment.appointmentNumber}" /></strong></dd>
        <dt>Patient</dt>
        <dd>
          <div class="patient-cell" style="margin-top:-0.4rem">
            <span class="patient-avatar"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg></span>
            <c:out value="${appointment.patient.fullName}" />
          </div>
        </dd>
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
  </div>
</main>
<%@ include file="fragments/footer.jspf" %>
</body>
</html>
