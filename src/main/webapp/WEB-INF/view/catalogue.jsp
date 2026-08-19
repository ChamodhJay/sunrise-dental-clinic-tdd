<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Catalogue and fees · Sunrise Dental Clinic</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<%@ include file="fragments/header.jspf" %>
<main class="page">
  <div class="page-heading"><h1>Treatment catalogue and fees</h1><p>Historical appointments retain their treatment and fee references.</p></div>
  <c:if test="${param.saved eq '1'}"><div class="alert success">Catalogue or fee change saved.</div></c:if>
  <c:if test="${not empty catalogueError}"><div class="alert error"><c:out value="${catalogueError}" /></div></c:if>
  <section class="card">
    <h2>Consultation fee</h2>
    <p>Current fee: <strong>Rs. <fmt:formatNumber value="${activeFee.consultationFee}" minFractionDigits="2" /></strong>, effective <c:out value="${activeFee.effectiveFrom}" />.</p>
    <form class="grid" action="${pageContext.request.contextPath}/catalogue" method="post">
      <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"><input type="hidden" name="action" value="fee">
      <div class="field span-8"><label for="consultationFee">New consultation fee (Rs.)</label><input id="consultationFee" name="consultationFee" type="number" min="0" step="0.01" required></div>
      <div class="field span-4"><label>&nbsp;</label><button class="button" type="submit">Activate new fee</button></div>
    </form>
  </section>
  <section class="card">
    <h2>Treatment types</h2>
    <div class="actions-grid">
      <c:forEach var="treatment" items="${treatments}">
        <form class="card" action="${pageContext.request.contextPath}/catalogue" method="post">
          <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
          <input type="hidden" name="action" value="treatment"><input type="hidden" name="treatmentId" value="${treatment.treatmentTypeId}">
          <div class="field"><label>Name</label><input name="name" type="text" maxlength="100" value="<c:out value='${treatment.name}' />" required></div>
          <div class="field"><label>Base price (Rs.)</label><input name="price" type="number" min="0" step="0.01" value="${treatment.basePrice}" required></div>
          <div class="field checkbox"><input id="active-${treatment.treatmentTypeId}" name="active" type="checkbox" value="true" <c:if test="${treatment.active}">checked</c:if>><label for="active-${treatment.treatmentTypeId}">Available for new bookings</label></div>
          <button class="button" type="submit">Save treatment</button>
        </form>
      </c:forEach>
    </div>
  </section>
  <section class="card">
    <h2>Add treatment type</h2>
    <form class="grid" action="${pageContext.request.contextPath}/catalogue" method="post">
      <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"><input type="hidden" name="action" value="treatment">
      <div class="field span-6"><label for="newName">Name</label><input id="newName" name="name" type="text" maxlength="100" required></div>
      <div class="field span-4"><label for="newPrice">Base price (Rs.)</label><input id="newPrice" name="price" type="number" min="0" step="0.01" required></div>
      <div class="field span-12 checkbox"><input id="newActive" name="active" type="checkbox" value="true" checked><label for="newActive">Available for new bookings</label></div>
      <div class="span-12"><button class="button" type="submit">Add treatment</button></div>
    </form>
  </section>
</main>
<%@ include file="fragments/footer.jspf" %>
</body>
</html>
