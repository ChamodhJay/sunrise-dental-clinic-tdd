<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>My appointments · Sunrise Dental Clinic</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<%@ include file="fragments/header.jspf" %>
<main class="page">

  <div class="page-topbar">
    <div class="breadcrumb">
      <span>Sunrise Dental Clinic</span>
      <span class="bc-sep">›</span>
      <span class="bc-current">My Appointments</span>
    </div>
    <div class="page-topbar-right">
      <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24" fill="none"
           stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M8 2v4"/><path d="M16 2v4"/>
        <rect width="18" height="18" x="3" y="4" rx="2"/><path d="M3 10h18"/>
      </svg>
      Appointment records
    </div>
  </div>

  <div class="page-body">

    <div class="page-heading">
      <div class="focus-label">Clinic Workspace</div>
      <h1>My assigned appointments</h1>
      <p>Patient details shown here are limited to appointments assigned to your dentist profile.</p>
    </div>

    <c:if test="${param.recorded eq '1'}">
      <div class="alert success">Treatment recorded and appointment completed.</div>
    </c:if>

    <%-- Stat pills --%>
    <div class="apt-stats">
      <div class="apt-stat">
        <div class="apt-stat-icon">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none"
               stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M8 2v4"/><path d="M16 2v4"/>
            <rect width="18" height="18" x="3" y="4" rx="2"/><path d="M3 10h18"/>
          </svg>
        </div>
        <div class="apt-stat-body">
          <strong>${appointments.size()}</strong>
          <span>Total assigned</span>
        </div>
      </div>
      <div class="apt-stat">
        <div class="apt-stat-icon">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none"
               stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
          </svg>
        </div>
        <div class="apt-stat-body">
          <strong><c:set var="sched" value="0"/><c:forEach var="a" items="${appointments}"><c:if test="${a.status.name() eq 'SCHEDULED'}"><c:set var="sched" value="${sched + 1}"/></c:if></c:forEach>${sched}</strong>
          <span>Scheduled</span>
        </div>
      </div>
      <div class="apt-stat">
        <div class="apt-stat-icon">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none"
               stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"/><path d="m9 12 2 2 4-4"/>
          </svg>
        </div>
        <div class="apt-stat-body">
          <strong><c:set var="comp" value="0"/><c:forEach var="a" items="${appointments}"><c:if test="${a.status.name() eq 'COMPLETED'}"><c:set var="comp" value="${comp + 1}"/></c:if></c:forEach>${comp}</strong>
          <span>Completed</span>
        </div>
      </div>
    </div>

    <%-- Appointments table --%>
    <section class="card">
      <div class="table-toolbar">
        <div class="table-toolbar-left">
          <h2>Appointment records</h2>
          <p class="card-subtitle">Sorted by most recent</p>
        </div>
        <div class="inline-search">
          <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24" fill="none"
               stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/>
          </svg>
          <input type="text" id="aptSearch" placeholder="Search patients…" oninput="filterTable(this.value)">
        </div>
      </div>
      <div class="table-wrap">
        <table id="aptTable">
          <thead>
            <tr>
              <th>Number</th>
              <th>Date / Time</th>
              <th>Patient</th>
              <th>Treatment</th>
              <th>Status</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="appointment" items="${appointments}">
              <tr>
                <td><c:out value="${appointment.appointmentNumber}" /></td>
                <td>
                  <c:out value="${appointment.appointmentDate}" /><br>
                  <span style="color:var(--muted);font-size:0.82rem"><c:out value="${appointment.appointmentTime}" /></span>
                </td>
                <td>
                  <div class="patient-cell">
                    <span class="patient-avatar"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg></span>
                    <c:out value="${appointment.patient.fullName}" />
                  </div>
                </td>
                <td><c:out value="${appointment.treatmentType.name}" /></td>
                <td><span class="status ${appointment.status}"><c:out value="${appointment.status}" /></span></td>
                <td>
                  <a class="table-view-link"
                     href="${pageContext.request.contextPath}/dentist/appointments?id=${appointment.appointmentId}">
                    View
                    <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none"
                         stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M7 7h10v10"/><path d="M7 17 17 7"/>
                    </svg>
                  </a>
                </td>
              </tr>
            </c:forEach>
            <c:if test="${empty appointments}">
              <tr><td colspan="6" style="text-align:center;color:var(--muted);padding:2rem">No appointments are assigned to you.</td></tr>
            </c:if>
          </tbody>
        </table>
      </div>
    </section>

    <%-- Selected appointment detail --%>
    <c:if test="${not empty selectedAppointment}">
      <section class="card">
        <h2><c:out value="${selectedAppointment.appointmentNumber}" /></h2>
        <p class="card-subtitle">Appointment details</p>
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

  </div><%-- /page-body --%>
</main>
<%@ include file="fragments/footer.jspf" %>
<script>
function filterTable(q) {
  q = q.toLowerCase();
  document.querySelectorAll('#aptTable tbody tr').forEach(function(row) {
    row.style.display = row.textContent.toLowerCase().includes(q) ? '' : 'none';
  });
}
</script>
</body>
</html>
