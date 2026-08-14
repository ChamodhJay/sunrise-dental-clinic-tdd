<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Dashboard · Sunrise Dental Clinic</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<%@ include file="fragments/header.jspf" %>
<main class="page">
  <div class="page-heading">
    <h1>Welcome, <c:out value="${sessionScope.authUser.fullName}" /></h1>
    <p>Signed in as <c:out value="${sessionScope.authUser.role}" />. Choose an authorized task.</p>
  </div>
  <div class="actions-grid">
    <c:if test="${sessionScope.authUser.role.name() eq 'RECEPTIONIST'}">
      <a class="card action-card" href="${pageContext.request.contextPath}/appointments?action=new">
        <strong>Register appointment</strong><span>Create the patient record, validate the slot, and generate an appointment number.</span>
      </a>
      <a class="card action-card" href="${pageContext.request.contextPath}/appointments">
        <strong>View appointment details</strong><span>Search by the unique appointment number.</span>
      </a>
      <a class="card action-card" href="${pageContext.request.contextPath}/billing">
        <strong>Calculate and print bill</strong><span>Generate the two-item receipt after treatment is completed.</span>
      </a>
    </c:if>
    <c:if test="${sessionScope.authUser.role.name() eq 'DENTIST'}">
      <a class="card action-card" href="${pageContext.request.contextPath}/dentist/appointments">
        <strong>Assigned appointments</strong><span>View assigned patient details and record completed treatment.</span>
      </a>
    </c:if>
    <c:if test="${sessionScope.authUser.role.name() eq 'CLINIC_MANAGER'}">
      <a class="card action-card" href="${pageContext.request.contextPath}/reports">
        <strong>Operational reports</strong><span>View daily appointment and billing summary reports.</span>
      </a>
      <a class="card action-card" href="${pageContext.request.contextPath}/catalogue">
        <strong>Treatment catalogue and fees</strong><span>Maintain treatment prices and the active consultation fee.</span>
      </a>
      <a class="card action-card" href="${pageContext.request.contextPath}/users">
        <strong>User account management</strong><span>Create staff accounts, reset passwords, and activate or deactivate access.</span>
      </a>
    </c:if>
    <a class="card action-card" href="${pageContext.request.contextPath}/help">
      <strong>Help instructions</strong><span>Read role-specific workflow and validation guidance.</span>
    </a>
  </div>
</main>
<%@ include file="fragments/footer.jspf" %>
</body>
</html>
