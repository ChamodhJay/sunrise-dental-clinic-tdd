<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Operational reports · Sunrise Dental Clinic</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<%@ include file="fragments/header.jspf" %>
<main class="page">
  <div class="page-topbar no-print">
    <div class="breadcrumb">
      <span>Sunrise Dental Clinic</span>
      <span class="bc-sep">›</span>
      <span class="bc-current">Operational Reports</span>
    </div>
  </div>

  <div class="page-body">
    <div class="page-heading no-print">
      <div class="focus-label">Clinic Workspace</div>
      <h1>Operational reports</h1>
      <p>Read-only daily scheduling and billing reports.</p>
    </div>

    <c:if test="${not empty reportError}"><div class="alert error"><c:out value="${reportError}" /></div></c:if>

    <div class="grid no-print">
      <form class="card span-6" action="${pageContext.request.contextPath}/reports" method="get">
        <input type="hidden" name="type" value="daily">
        <h2>Daily appointments</h2>
        <div class="field"><label for="date">Report date</label><input id="date" name="date" type="date" value="${empty reportDate ? today : reportDate}" required></div>
        <button class="button" type="submit">Generate daily report</button>
      </form>
      <form class="card span-6" action="${pageContext.request.contextPath}/reports" method="get">
        <input type="hidden" name="type" value="billing">
        <h2>Billing summary</h2>
        <div class="grid">
          <div class="field span-6"><label for="from">From</label><input id="from" name="from" type="date" value="${fromDate}" required></div>
          <div class="field span-6"><label for="to">To</label><input id="to" name="to" type="date" value="${toDate}" required></div>
        </div>
        <button class="button" type="submit">Generate billing report</button>
      </form>
    </div>

    <c:if test="${not empty dailyAppointments or param.type eq 'daily'}">
      <section class="card">
        <h2>Appointments for <c:out value="${reportDate}" /></h2>
        <div class="table-wrap">
          <table>
            <thead><tr><th>Time</th><th>Number</th><th>Patient</th><th>Dentist</th><th>Treatment</th><th>Status</th></tr></thead>
            <tbody>
              <c:forEach var="appointment" items="${dailyAppointments}">
                <tr>
                  <td><c:out value="${appointment.appointmentTime}" /></td>
                  <td><c:out value="${appointment.appointmentNumber}" /></td>
                  <td>
                    <div class="patient-cell">
                      <span class="patient-avatar"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg></span>
                      <c:out value="${appointment.patient.fullName}" />
                    </div>
                  </td>
                  <td><c:out value="${appointment.dentist.fullName}" /></td>
                  <td><c:out value="${appointment.treatmentType.name}" /></td>
                  <td><span class="status ${appointment.status}"><c:out value="${appointment.status}" /></span></td>
                </tr>
              </c:forEach>
              <c:if test="${empty dailyAppointments}"><tr><td colspan="6" style="text-align:center;padding:2rem">No appointments for this date.</td></tr></c:if>
            </tbody>
          </table>
        </div>
        <button class="button no-print top-gap" type="button" onclick="window.print()">Print report</button>
      </section>
    </c:if>

    <c:if test="${not empty billingSummary or param.type eq 'billing'}">
      <section class="card">
        <h2>Billing summary: <c:out value="${fromDate}" /> to <c:out value="${toDate}" /></h2>
        <div class="table-wrap">
          <table>
            <thead><tr><th>Treatment</th><th>Bills</th><th class="money">Total (Rs.)</th></tr></thead>
            <tbody>
              <c:forEach var="row" items="${billingSummary}">
                <tr>
                  <td><c:out value="${row.treatmentName}" /></td>
                  <td><c:out value="${row.billCount}" /></td>
                  <td class="money"><fmt:formatNumber value="${row.totalAmount}" minFractionDigits="2" /></td>
                </tr>
              </c:forEach>
              <c:if test="${empty billingSummary}"><tr><td colspan="3" style="text-align:center;padding:2rem">No billing data for this period.</td></tr></c:if>
            </tbody>
          </table>
        </div>
        <button class="button no-print top-gap" type="button" onclick="window.print()">Print report</button>
      </section>
    </c:if>

  </div>
</main>
<%@ include file="fragments/footer.jspf" %>
</body>
</html>
