<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Register appointment · Sunrise Dental Clinic</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<%@ include file="fragments/header.jspf" %>
<main class="page">
  <div class="page-heading">
    <h1>Register new appointment</h1>
    <p>The appointment number is generated automatically after all checks pass.</p>
  </div>
  <c:if test="${not empty formError}"><div class="alert error"><c:out value="${formError}" /></div></c:if>
  <form class="card" action="${pageContext.request.contextPath}/appointments" method="post">
    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
    <div class="grid">
      <div class="field span-6">
        <label for="patientName">Patient full name</label>
        <input id="patientName" name="patientName" type="text" maxlength="100"
               value="<c:out value='${param.patientName}' />" required>
        <c:if test="${not empty fieldErrors.patientName}"><p class="field-error"><c:out value="${fieldErrors.patientName}" /></p></c:if>
      </div>
      <div class="field span-6">
        <label for="contactNumber">Contact number</label>
        <input id="contactNumber" name="contactNumber" type="tel" maxlength="20"
               placeholder="0771234567 or +94771234567" value="<c:out value='${param.contactNumber}' />" required>
        <c:if test="${not empty fieldErrors.contactNumber}"><p class="field-error"><c:out value="${fieldErrors.contactNumber}" /></p></c:if>
      </div>
      <div class="field span-12">
        <label for="address">Address</label>
        <textarea id="address" name="address" maxlength="200" required><c:out value="${param.address}" /></textarea>
        <c:if test="${not empty fieldErrors.address}"><p class="field-error"><c:out value="${fieldErrors.address}" /></p></c:if>
      </div>
      <div class="field span-6">
        <label for="dentistId">Dentist</label>
        <select id="dentistId" name="dentistId" required>
          <option value="">Select an available dentist</option>
          <c:forEach var="dentist" items="${dentists}">
            <option value="${dentist.dentistId}" <c:if test="${param.dentistId eq dentist.dentistId.toString()}">selected</c:if>>
              <c:out value="${dentist.fullName}" />
            </option>
          </c:forEach>
        </select>
        <c:if test="${not empty fieldErrors.dentistId}"><p class="field-error"><c:out value="${fieldErrors.dentistId}" /></p></c:if>
      </div>
      <div class="field span-6">
        <label for="treatmentTypeId">Treatment type</label>
        <select id="treatmentTypeId" name="treatmentTypeId" required>
          <option value="">Select treatment</option>
          <c:forEach var="treatment" items="${treatments}">
            <option value="${treatment.treatmentTypeId}" <c:if test="${param.treatmentTypeId eq treatment.treatmentTypeId.toString()}">selected</c:if>>
              <c:out value="${treatment.name}" /> — Rs. <fmt:formatNumber value="${treatment.basePrice}" minFractionDigits="2" />
            </option>
          </c:forEach>
        </select>
        <c:if test="${not empty fieldErrors.treatmentTypeId}"><p class="field-error"><c:out value="${fieldErrors.treatmentTypeId}" /></p></c:if>
      </div>
      <div class="field span-6">
        <label for="appointmentDate">Appointment date</label>
        <input id="appointmentDate" name="appointmentDate" type="date" min="${minimumDate}"
               value="<c:out value='${param.appointmentDate}' />" required>
        <p class="hint">Monday to Friday; future dates only.</p>
        <c:if test="${not empty fieldErrors.appointmentDate}"><p class="field-error"><c:out value="${fieldErrors.appointmentDate}" /></p></c:if>
      </div>
      <div class="field span-6">
        <label for="appointmentTime">Appointment time</label>
        <input id="appointmentTime" name="appointmentTime" type="time" min="09:00" max="18:00" step="1800"
               value="<c:out value='${param.appointmentTime}' />" required>
        <p class="hint">30-minute slots from 09:00 through 18:00.</p>
        <c:if test="${not empty fieldErrors.appointmentTime}"><p class="field-error"><c:out value="${fieldErrors.appointmentTime}" /></p></c:if>
      </div>
    </div>
    <div class="button-row">
      <button class="button" type="submit">Validate and register</button>
      <a class="button secondary" href="${pageContext.request.contextPath}/dashboard">Cancel</a>
    </div>
  </form>
</main>
<%@ include file="fragments/footer.jspf" %>
</body>
</html>
