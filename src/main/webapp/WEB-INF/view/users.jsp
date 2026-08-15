<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>User accounts · Sunrise Dental Clinic</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<%@ include file="fragments/header.jspf" %>
<main class="page">
  <div class="page-heading">
    <h1>User account management</h1>
    <p>Create staff logins, reset passwords, and control account access.</p>
  </div>

  <c:if test="${param.result eq 'created'}"><div class="alert success" role="status">The user account was created.</div></c:if>
  <c:if test="${param.result eq 'passwordReset'}"><div class="alert success" role="status">The password was reset securely.</div></c:if>
  <c:if test="${param.result eq 'statusChanged'}"><div class="alert success" role="status">The account status was updated.</div></c:if>
  <c:if test="${not empty accountError}"><div class="alert error" role="alert"><c:out value="${accountError}" /></div></c:if>
  <c:if test="${not empty fieldErrors.userId}"><div class="alert error" role="alert"><c:out value="${fieldErrors.userId}" /></div></c:if>

  <section class="card">
    <h2>Create user account</h2>
    <p class="hint">Dentist accounts automatically receive the linked dentist profile required for appointment assignment.</p>
    <form class="grid top-gap" action="${pageContext.request.contextPath}/users" method="post">
      <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
      <input type="hidden" name="action" value="create">
      <div class="field span-6">
        <label for="username">Username</label>
        <input id="username" name="username" type="text" maxlength="50" autocomplete="off"
               value="<c:out value='${submittedAction eq "create" ? param.username : ""}' />" required>
        <c:if test="${submittedAction eq 'create' and not empty fieldErrors.username}"><p class="field-error"><c:out value="${fieldErrors.username}" /></p></c:if>
      </div>
      <div class="field span-6">
        <label for="fullName">Full name</label>
        <input id="fullName" name="fullName" type="text" maxlength="100" autocomplete="off"
               value="<c:out value='${submittedAction eq "create" ? param.fullName : ""}' />" required>
        <c:if test="${submittedAction eq 'create' and not empty fieldErrors.fullName}"><p class="field-error"><c:out value="${fieldErrors.fullName}" /></p></c:if>
      </div>
      <div class="field span-4">
        <label for="role">Role</label>
        <select id="role" name="role" required>
          <option value="">Select a role</option>
          <option value="RECEPTIONIST" <c:if test="${param.role eq 'RECEPTIONIST'}">selected</c:if>>Receptionist</option>
          <option value="DENTIST" <c:if test="${param.role eq 'DENTIST'}">selected</c:if>>Dentist</option>
          <option value="CLINIC_MANAGER" <c:if test="${param.role eq 'CLINIC_MANAGER'}">selected</c:if>>Clinic Manager</option>
        </select>
        <c:if test="${submittedAction eq 'create' and not empty fieldErrors.role}"><p class="field-error"><c:out value="${fieldErrors.role}" /></p></c:if>
      </div>
      <div class="field span-4">
        <label for="password">Initial password</label>
        <input id="password" name="password" type="password" minlength="10" maxlength="128" autocomplete="new-password" required>
        <c:if test="${submittedAction eq 'create' and not empty fieldErrors.password}"><p class="field-error"><c:out value="${fieldErrors.password}" /></p></c:if>
      </div>
      <div class="field span-4">
        <label for="confirmPassword">Confirm password</label>
        <input id="confirmPassword" name="confirmPassword" type="password" minlength="10" maxlength="128" autocomplete="new-password" required>
        <c:if test="${submittedAction eq 'create' and not empty fieldErrors.confirmPassword}"><p class="field-error"><c:out value="${fieldErrors.confirmPassword}" /></p></c:if>
      </div>
      <div class="span-12">
        <p class="hint">Use 10-128 characters with uppercase, lowercase, a number, and a symbol.</p>
        <button class="button" type="submit">Create account</button>
      </div>
    </form>
  </section>

  <section class="card">
    <h2>System user accounts</h2>
    <div class="table-wrap">
      <table>
        <thead><tr><th>User ID</th><th>Username</th><th>Full name</th><th>Role</th><th>Status</th><th>Created</th><th>Account action</th></tr></thead>
        <tbody>
        <c:forEach var="user" items="${users}">
          <tr>
            <td class="identifier"><c:out value="${user.userId}" /></td>
            <td><strong><c:out value="${user.username}" /></strong></td>
            <td><c:out value="${user.fullName}" /></td>
            <td><c:out value="${user.role}" /></td>
            <td><span class="status ${user.active ? 'ACTIVE' : 'INACTIVE'}">${user.active ? 'ACTIVE' : 'INACTIVE'}</span></td>
            <td><c:out value="${user.createdAt}" /></td>
            <td>
              <c:choose>
                <c:when test="${sessionScope.authUser.userId eq user.userId}"><span class="hint">Current account</span></c:when>
                <c:otherwise>
                  <form action="${pageContext.request.contextPath}/users" method="post">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="userId" value="${user.userId}">
                    <input type="hidden" name="action" value="${user.active ? 'deactivate' : 'activate'}">
                    <button class="button ${user.active ? 'danger' : 'secondary'}" type="submit">${user.active ? 'Deactivate' : 'Activate'}</button>
                  </form>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr class="sub-row">
            <td colspan="7">
              <form class="grid compact-form" action="${pageContext.request.contextPath}/users" method="post">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="resetPassword">
                <input type="hidden" name="userId" value="${user.userId}">
                <div class="field span-4"><label for="new-password-${user.userId}">New password for <c:out value="${user.username}" /></label><input id="new-password-${user.userId}" name="password" type="password" minlength="10" maxlength="128" autocomplete="new-password" required>
                  <c:if test="${submittedAction eq 'resetPassword' and targetUserId eq user.userId.toString() and not empty fieldErrors.password}"><p class="field-error"><c:out value="${fieldErrors.password}" /></p></c:if>
                </div>
                <div class="field span-4"><label for="confirm-password-${user.userId}">Confirm new password</label><input id="confirm-password-${user.userId}" name="confirmPassword" type="password" minlength="10" maxlength="128" autocomplete="new-password" required>
                  <c:if test="${submittedAction eq 'resetPassword' and targetUserId eq user.userId.toString() and not empty fieldErrors.confirmPassword}"><p class="field-error"><c:out value="${fieldErrors.confirmPassword}" /></p></c:if>
                </div>
                <div class="field span-4"><label>&nbsp;</label><button class="button secondary" type="submit">Reset password</button></div>
              </form>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty users}"><tr><td colspan="7">No user accounts are available.</td></tr></c:if>
        </tbody>
      </table>
    </div>
  </section>
</main>
<%@ include file="fragments/footer.jspf" %>
</body>
</html>
