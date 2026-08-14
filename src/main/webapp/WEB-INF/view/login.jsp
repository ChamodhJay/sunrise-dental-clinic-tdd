<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Log in · Sunrise Dental Clinic</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body class="login-page">
  <main class="login-card">
    <h1>Staff login</h1>
    <p class="subtitle">Sunrise Dental Clinic appointment and patient management</p>
    <c:if test="${param.loggedOut eq '1'}">
      <div class="alert success" role="status">You have exited the system safely.</div>
    </c:if>
    <c:if test="${param.accountDisabled eq '1'}">
      <div class="alert error" role="alert">This account is inactive. Contact the clinic manager.</div>
    </c:if>
    <c:if test="${not empty loginError}">
      <div class="alert error" role="alert"><c:out value="${loginError}" /></div>
    </c:if>
    <form action="${pageContext.request.contextPath}/login" method="post">
      <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
      <div class="field">
        <label for="username">Username</label>
        <input id="username" name="username" type="text" maxlength="50"
               value="<c:out value='${submittedUsername}' />" autocomplete="username" required autofocus>
      </div>
      <div class="field">
        <label for="password">Password</label>
        <input id="password" name="password" type="password" maxlength="128"
               autocomplete="current-password" required>
      </div>
      <button class="button" type="submit">Log in securely</button>
    </form>
  </main>
</body>
</html>
