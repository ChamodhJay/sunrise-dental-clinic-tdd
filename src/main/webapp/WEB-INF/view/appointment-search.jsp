<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Find appointment · Sunrise Dental Clinic</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<%@ include file="fragments/header.jspf" %>
<main class="page">
  <div class="page-heading"><h1>View appointment details</h1><p>Use the exact unique appointment number.</p></div>
  <c:if test="${not empty searchError}"><div class="alert error"><c:out value="${searchError}" /></div></c:if>
  <form class="card" action="${pageContext.request.contextPath}/appointments" method="get">
    <input type="hidden" name="action" value="view">
    <div class="field">
      <label for="number">Appointment number</label>
      <input id="number" name="number" type="text" maxlength="30" placeholder="APT-260815-0001"
             value="<c:out value='${param.number}' />" required autofocus>
    </div>
    <button class="button" type="submit">Search</button>
  </form>
</main>
<%@ include file="fragments/footer.jspf" %>
</body>
</html>
