<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Assigned appointments · Sunrise Dental Clinic</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<%@ include file="fragments/header.jspf" %>
<main class="page">
  <div class="page-heading"><h1>My assigned appointments</h1><p>Patient details shown here are limited to appointments assigned to your dentist profile.</p></div>
  <c:if test="${param.recorded eq '1'}"><div class="alert success">Treatment recorded and appointment completed atomically.</div></c:if>
  <section class="card">
    <div class="table-wrap">
      <table>
        <thead><tr><th>Number</th><th>Date / time</th><th>Patient</th><th>Treatment</th><th>Status</th><th></th></tr></thead>
        <tbody>
          <c:forEach var="appointment" items="${appointments}">
            <tr>
              <td><c:out value="${appointment.appointmentNumber}" /></td>
              <td><c:out value="${appointment.appointmentDate}" /> <c:out value="${appointment.appointmentTime}" /></td>
              <td><c:out value="${appointment.patient.fullName}" /></td>
              <td><c:out value="${appointment.treatmentType.name}" /></td>
              <td><span class="status ${appointment.status}"><c:out value="${appointment.status}" /></span></td>
              <td><a href="${pageContext.request.contextPath}/dentist/appointments?id=${appointment.appointmentId}">View</a></td>
            </tr>
          </c:forEach>
          <c:if test="${empty appointments}"><tr><td colspan="6">No appointments are assigned to you.</td></tr></c:if>
        </tbody>
      </table>
    </div>
  </section>
  <c:if test="${not empty selectedAppointment}">
    <section class="card">
      <h2><c:out value="${selectedAppointment.appointmentNumber}" /></h2>
      <dl class="details">
        <dt>Patient</dt><dd><c:out value="${selectedAppointment.patient.fullName}" /></dd>
        <dt>Contact</dt><dd><c:out value="${selectedAppointment.patient.contactNumber}" /></dd>
        <dt>Address</dt><dd><c:out value="${selectedAppointment.patient.address}" /></dd>
        <dt>Treatment</dt><dd><c:out value="${selectedAppointment.treatmentType.name}" /></dd>
        <dt>Date and time</dt><dd><c:out value="${selectedAppointment.appointmentDate}" /> at <c:out value="${selectedAppointment.appointmentTime}" /></dd>
      </dl>
      <c:choose>
        <c:when test="${selectedAppointment.status.name() eq 'SCHEDULED'}">
          <form class="top-gap" action="${pageContext.request.contextPath}/dentist/appointments" method="post">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <input type="hidden" name="appointmentId" value="${selectedAppointment.appointmentId}">
            <div class="field">
              <label for="diagnosis">Diagnosis</label>
              <textarea id="diagnosis" name="diagnosis" maxlength="500" required></textarea>
            </div>
            <div class="field">
              <label for="treatmentNotes">Treatment notes</label>
              <textarea id="treatmentNotes" name="treatmentNotes" maxlength="2000" required></textarea>
            </div>
            <button class="button" type="submit">Record treatment and complete appointment</button>
          </form>
        </c:when>
        <c:otherwise>
          <div class="alert info top-gap">Treatment was completed at <c:out value="${selectedAppointment.treatmentRecord.completedAt}" />.</div>
          <dl class="details">
            <dt>Diagnosis</dt><dd><c:out value="${selectedAppointment.treatmentRecord.diagnosis}" /></dd>
            <dt>Notes</dt><dd><c:out value="${selectedAppointment.treatmentRecord.treatmentNotes}" /></dd>
          </dl>
        </c:otherwise>
      </c:choose>
    </section>
  </c:if>
</main>
<%@ include file="fragments/footer.jspf" %>
</body>
</html>
