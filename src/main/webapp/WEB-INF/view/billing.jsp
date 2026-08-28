<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Patient billing · Sunrise Dental Clinic</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<%@ include file="fragments/header.jspf" %>
<main class="page">
  <div class="page-topbar no-print">
    <div class="breadcrumb">
      <span>Sunrise Dental Clinic</span>
      <span class="bc-sep">›</span>
      <span class="bc-current">Billing</span>
    </div>
  </div>

  <div class="page-body">
    <div class="page-heading no-print">
      <div class="focus-label">Clinic Workspace</div>
      <h1>Calculate and print patient bill</h1>
      <p>A bill requires a completed appointment and recorded treatment.</p>
    </div>

    <c:if test="${not empty billingError}"><div class="alert error no-print"><c:out value="${billingError}" /></div></c:if>
    <c:if test="${param.created eq '1'}"><div class="alert success no-print">Bill calculated and saved successfully.</div></c:if>

    <c:if test="${empty bill}">
      <form class="card no-print" action="${pageContext.request.contextPath}/billing" method="post" style="max-width:500px">
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
        <div class="field">
          <label for="appointmentNumber">Appointment number</label>
          <div class="inline-search" style="width:100%">
            <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>
            <input id="appointmentNumber" name="appointmentNumber" type="text" maxlength="30" style="width:100%"
                   placeholder="APT-260815-0001" value="<c:out value='${param.appointmentNumber}' />" required autofocus>
          </div>
        </div>
        <button class="button" type="submit">Calculate and save bill</button>
      </form>
    </c:if>

    <c:if test="${not empty bill}">
      <section class="card" aria-label="Patient bill">
        <div class="receipt-header">
          <h2>Sunrise Dental Clinic</h2>
          <p>456 Galle Road, Colombo 03 · +94 11 234 5678</p>
          <strong>Patient Bill / Receipt</strong>
        </div>
        <dl class="details">
          <dt>Bill number</dt><dd><c:out value="${bill.billNumber}" /></dd>
          <dt>Appointment</dt><dd><c:out value="${bill.appointment.appointmentNumber}" /></dd>
          <dt>Patient</dt><dd><c:out value="${bill.appointment.patient.fullName}" /> · <c:out value="${bill.appointment.patient.contactNumber}" /></dd>
          <dt>Dentist</dt><dd><c:out value="${bill.appointment.dentist.fullName}" /></dd>
          <dt>Appointment date</dt><dd><c:out value="${bill.appointment.appointmentDate}" /> at <c:out value="${bill.appointment.appointmentTime}" /></dd>
          <dt>Generated</dt><dd><c:out value="${bill.generatedAt}" /> by <c:out value="${bill.generatedBy.fullName}" /></dd>
          <dt>Status</dt><dd><span class="status ${bill.status}"><c:out value="${bill.status}" /></span></dd>
        </dl>
        <div class="table-wrap top-gap">
          <table>
            <thead><tr><th>Charge</th><th>Description</th><th class="money">Amount (Rs.)</th></tr></thead>
            <tbody>
              <c:forEach var="line" items="${bill.lines}">
                <tr><td><c:out value="${line.lineType}" /></td><td><c:out value="${line.description}" /></td>
                  <td class="money"><fmt:formatNumber value="${line.amount}" minFractionDigits="2" /></td></tr>
              </c:forEach>
              <tr class="total-row"><td colspan="2">Total amount due</td>
                <td class="money"><fmt:formatNumber value="${bill.totalAmount}" minFractionDigits="2" /></td></tr>
            </tbody>
          </table>
        </div>
        <p style="margin-top:2rem">Payment terms: Due upon receipt. Thank you for choosing Sunrise Dental Clinic.</p>
        <div class="button-row no-print top-gap">
          <form action="${pageContext.request.contextPath}/billing" method="post">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <input type="hidden" name="action" value="print">
            <input type="hidden" name="appointmentNumber" value="${bill.appointment.appointmentNumber}">
            <button class="button" type="submit">Print bill / receipt</button>
          </form>
          <a class="button secondary" href="${pageContext.request.contextPath}/billing">Another bill</a>
        </div>
      </section>
    </c:if>

  </div>
</main>
<%@ include file="fragments/footer.jspf" %>
<c:if test="${printNow}"><script>window.print();</script></c:if>
</body>
</html>
