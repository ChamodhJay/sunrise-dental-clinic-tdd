<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
  <div class="page-topbar">
    <div class="breadcrumb">
      <span>Sunrise Dental Clinic</span>
      <span class="bc-sep">›</span>
      <span class="bc-current">User Accounts</span>
    </div>
  </div>

  <div class="page-body">
    <div class="page-heading">
      <div class="focus-label">Clinic Workspace</div>
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
      <p class="card-subtitle">Dentist accounts automatically receive the linked dentist profile required for appointment assignment.</p>
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
      <div class="table-toolbar">
        <div class="table-toolbar-left">
          <h2>System user accounts</h2>
        </div>
      </div>
      <div class="table-wrap">
        <table>
          <thead><tr><th>User ID</th><th>Username</th><th>Full name</th><th>Role</th><th>Status</th><th>Created</th><th>Account action</th></tr></thead>
          <tbody>
          <c:forEach var="user" items="${users}">
            <tr>
              <td class="identifier" title="<c:out value='${user.userId}' />"><c:out value="${fn:substring(user.userId, 0, 5)}" /></td>
              <td><strong><c:out value="${user.username}" /></strong></td>
              <td>
                <div class="patient-cell">
                  <span class="patient-avatar"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg></span>
                  <c:out value="${user.fullName}" />
                </div>
              </td>
              <td><c:out value="${user.role}" /></td>
              <td><span class="status ${user.active ? 'ACTIVE' : 'INACTIVE'}">${user.active ? 'ACTIVE' : 'INACTIVE'}</span></td>
              <td><c:out value="${user.createdAt}" /></td>
              <td>
                <c:choose>
                  <c:when test="${sessionScope.authUser.userId eq user.userId}"><span class="hint">Current account</span></c:when>
                  <c:otherwise>
                    <div class="action-group">
                      <button type="button" class="button secondary compact btn-icon-text" onclick="openResetModal('${user.userId}')">
                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="18" height="11" x="3" y="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                        Reset PW
                      </button>
                      <form action="${pageContext.request.contextPath}/users" method="post" style="margin:0">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                        <input type="hidden" name="userId" value="${user.userId}">
                        <input type="hidden" name="action" value="${user.active ? 'deactivate' : 'activate'}">
                        <button class="button ${user.active ? 'danger' : 'secondary'} compact" type="submit">${user.active ? 'Deactivate' : 'Activate'}</button>
                      </form>
                    </div>
                  </c:otherwise>
                </c:choose>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty users}"><tr><td colspan="7" style="text-align:center;padding:2rem">No user accounts are available.</td></tr></c:if>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</main>

<div id="resetModal" class="modal-overlay" role="dialog" aria-modal="true" aria-labelledby="resetModalTitle">
  <div class="modal">
    <div class="modal-header">
      <h3 id="resetModalTitle">Reset User Password</h3>
      <button type="button" class="modal-close" onclick="closeResetModal()" aria-label="Close password reset">&times;</button>
    </div>
    <div class="modal-body">
      <form action="${pageContext.request.contextPath}/users" method="post">
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
        <input type="hidden" name="action" value="resetPassword">
        <input type="hidden" id="resetModalUserId" name="userId" value="<c:out value='${targetUserId}' />">
        
        <div class="field">
          <label for="newPassword">New password</label>
          <input id="newPassword" name="password" type="password" minlength="10" maxlength="128" autocomplete="new-password" required>
          <c:if test="${submittedAction eq 'resetPassword' and not empty fieldErrors.password}"><p class="field-error"><c:out value="${fieldErrors.password}" /></p></c:if>
        </div>
        
        <div class="field">
          <label for="resetConfirmPassword">Confirm new password</label>
          <input id="resetConfirmPassword" name="confirmPassword" type="password" minlength="10" maxlength="128" autocomplete="new-password" required>
          <c:if test="${submittedAction eq 'resetPassword' and not empty fieldErrors.confirmPassword}"><p class="field-error"><c:out value="${fieldErrors.confirmPassword}" /></p></c:if>
        </div>
        
        <div class="button-row" style="margin-top: 2rem; justify-content: flex-end;">
          <button type="button" class="button secondary" onclick="closeResetModal()">Cancel</button>
          <button type="submit" class="button">Reset password</button>
        </div>
      </form>
    </div>
  </div>
</div>

<script>
  const resetModal = document.getElementById('resetModal');
  let resetReturnFocus = null;

  function openResetModal(userId) {
    resetReturnFocus = document.activeElement;
    document.getElementById('resetModalUserId').value = userId;
    resetModal.classList.add('open');
    document.getElementById('newPassword').focus();
  }

  function closeResetModal() {
    resetModal.classList.remove('open');
    if (resetReturnFocus) {
      resetReturnFocus.focus();
    }
  }

  resetModal.addEventListener('click', function (event) {
    if (event.target === resetModal) {
      closeResetModal();
    }
  });

  document.addEventListener('keydown', function (event) {
    if (event.key === 'Escape' && resetModal.classList.contains('open')) {
      closeResetModal();
    }
  });

  <c:if test="${submittedAction eq 'resetPassword'}">
  openResetModal(document.getElementById('resetModalUserId').value);
  </c:if>
</script>
<%@ include file="fragments/footer.jspf" %>
</body>
</html>
