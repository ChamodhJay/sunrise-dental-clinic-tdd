<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <!doctype html>
    <html lang="en">

    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1">
      <title>Find appointment · Sunrise Dental Clinic</title>
      <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
    </head>

    <body>
      <%@ include file="fragments/header.jspf" %>
        <main class="page">
          <div class="page-topbar">
            <div class="breadcrumb">
              <span>Sunrise Dental Clinic</span>
              <span class="bc-sep">›</span>
              <span class="bc-current">Find Appointment</span>
            </div>
          </div>

          <div class="page-body">
            <div class="page-heading">
              <div class="focus-label">Clinic Workspace</div>
              <h1>View appointment details</h1>
              <p>Use the exact unique appointment number.</p>
            </div>

            <c:if test="${not empty searchError}">
              <div class="alert error">
                <c:out value="${searchError}" />
              </div>
            </c:if>
            <form class="card" action="${pageContext.request.contextPath}/appointments" method="get"
              style="max-width:500px">
              <input type="hidden" name="action" value="view">
              <div class="field">
                <label for="number">Appointment number</label>
                <div class="inline-search" style="width:100%">
                  <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24" fill="none"
                    stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="11" cy="11" r="8" />
                    <path d="m21 21-4.3-4.3" />
                  </svg>
                  <input id="number" style="width:100%" name="number" type="text" maxlength="30"
                    placeholder="APT-260815-0001" value="<c:out value='${param.number}' />" required autofocus>
                </div>
              </div>
              <button class="button" type="submit">Search</button>
            </form>
          </div>
        </main>
        <%@ include file="fragments/footer.jspf" %>
    </body>

    </html>