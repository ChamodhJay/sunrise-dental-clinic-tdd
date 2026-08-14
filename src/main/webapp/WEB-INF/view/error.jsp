<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Request error · Sunrise Dental Clinic</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<c:if test="${not empty sessionScope.authUser}"><%@ include file="fragments/header.jspf" %></c:if>
<main class="page">
  <section class="card">
    <h1>Unable to complete the request</h1>
    <div class="alert error"><c:out value="${errorMessage}" default="An unexpected error occurred." /></div>
    <c:choose><c:when test="${not empty sessionScope.authUser}"><a class="button" href="${pageContext.request.contextPath}/dashboard">Return to dashboard</a></c:when>
      <c:otherwise><a class="button" href="${pageContext.request.contextPath}/login">Return to login</a></c:otherwise></c:choose>
  </section>
</main>
</body>
</html>
