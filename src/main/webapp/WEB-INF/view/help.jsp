<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Help · Sunrise Dental Clinic</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<%@ include file="fragments/header.jspf" %>
<main class="page">

  <div class="page-topbar">
    <div class="breadcrumb">
      <span>Sunrise Dental Clinic</span>
      <span class="bc-sep">›</span>
      <span class="bc-current">Help Center</span>
    </div>
    <div class="page-topbar-right">
      <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24" fill="none"
           stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="12" cy="12" r="10"/><path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/><path d="M12 17h.01"/>
      </svg>
      Guides and support
    </div>
  </div>

  <div class="page-body">
    <div class="page-heading">
      <div class="focus-label">Clinic Workspace</div>
      <h1>Help instructions</h1>
      <p>Role-specific workflow and validation guidance for <strong><c:out value="${sessionScope.authUser.role}" /></strong> users.</p>
    </div>

    <%-- Common guidance --%>
    <section class="card help-section">
      <div class="help-section-header">
        <div class="help-section-icon">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none"
               stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
          </svg>
        </div>
        <div>
          <h2>Common guidance</h2>
          <p>Applies to every role in the clinic workspace</p>
        </div>
      </div>
      <div class="help-row">
        <div class="help-row-icon">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
               stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 2l-2 2m-7.61 7.61a5.5 5.5 0 1 1-7.778 7.778 5.5 5.5 0 0 1 7.777-7.777zm0 0L15.5 7.5m0 0 3 3L22 7l-3-3m-3.5 3.5L19 4"/>
          </svg>
        </div>
        <span class="help-row-num">01</span>
        <span class="help-row-text">Never share your username or password, and use Sign out when your work is complete.</span>
      </div>
      <div class="help-row">
        <div class="help-row-icon">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
               stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M4 7V4h16v3"/><path d="M9 20h6"/><path d="M12 4v16"/>
          </svg>
        </div>
        <span class="help-row-num">02</span>
        <span class="help-row-text">Appointment numbers are the official lookup key. Copy them exactly from the confirmation.</span>
      </div>
      <div class="help-row">
        <div class="help-row-icon">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
               stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/>
          </svg>
        </div>
        <span class="help-row-num">03</span>
        <span class="help-row-text">If a form reports an error, correct the identified field; valid input is preserved.</span>
      </div>
      <div class="help-row">
        <div class="help-row-icon">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
               stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="6 9 6 2 18 2 18 9"/><path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"/>
            <rect width="12" height="8" x="6" y="14"/>
          </svg>
        </div>
        <span class="help-row-num">04</span>
        <span class="help-row-text">Use the browser print dialog for confirmations, details, receipts, and reports.</span>
      </div>
    </section>

    <%-- Receptionist workflow --%>
    <c:if test="${sessionScope.authUser.role.name() eq 'RECEPTIONIST'}">
      <section class="card help-section">
        <div class="help-section-header">
          <div class="help-section-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none"
                 stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M8 2v4"/><path d="M16 2v4"/>
              <path d="M21 13V6a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h8"/>
              <path d="M3 10h18"/><path d="M19 16v6"/><path d="M16 19h6"/>
            </svg>
          </div>
          <div>
            <h2>Receptionist workflow</h2>
            <p>Steps specific to your receptionist profile</p>
          </div>
        </div>
        <div class="help-row">
          <div class="help-row-icon"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M8 2v4"/><path d="M16 2v4"/><rect width="18" height="18" x="3" y="4" rx="2"/><path d="M3 10h18"/></svg></div>
          <span class="help-row-num">01</span>
          <span class="help-row-text">Register the patient and select an active dentist, treatment, future weekday, and 30-minute clinic slot.</span>
        </div>
        <div class="help-row">
          <div class="help-row-icon"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg></div>
          <span class="help-row-num">02</span>
          <span class="help-row-text">If the patient contact number already exists, the patient record is reused and its current name/address are updated.</span>
        </div>
        <div class="help-row">
          <div class="help-row-icon"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg></div>
          <span class="help-row-num">03</span>
          <span class="help-row-text">Search using the generated appointment number. Billing becomes available only after the assigned dentist completes treatment.</span>
        </div>
        <div class="help-row">
          <div class="help-row-icon"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="20" height="14" x="2" y="5" rx="2"/><line x1="2" x2="22" y1="10" y2="10"/></svg></div>
          <span class="help-row-num">04</span>
          <span class="help-row-text">A bill always contains exactly two charges: treatment price and the active consultation fee.</span>
        </div>
      </section>
    </c:if>

    <%-- Dentist workflow --%>
    <c:if test="${sessionScope.authUser.role.name() eq 'DENTIST'}">
      <section class="card help-section">
        <div class="help-section-header">
          <div class="help-section-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none"
                 stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M12.5 15.5C10.5 13.5 8 14.5 8.5 18C9 21.5 10.5 24 11.5 27C12 29 13 30 14 29"/>
            </svg>
          </div>
          <div>
            <h2>Dentist workflow</h2>
            <p>Steps specific to your dentist profile</p>
          </div>
        </div>
        <div class="help-row">
          <div class="help-row-icon"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 12h18"/><path d="M3 6h18"/><path d="M3 18h18"/></svg></div>
          <span class="help-row-num">01</span>
          <span class="help-row-text">Open My appointments to see only records assigned to your dentist identity.</span>
        </div>
        <div class="help-row">
          <div class="help-row-icon"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4Z"/></svg></div>
          <span class="help-row-num">02</span>
          <span class="help-row-text">Select a scheduled appointment, record both diagnosis and treatment notes, and submit once.</span>
        </div>
        <div class="help-row">
          <div class="help-row-icon"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6 9 17l-5-5"/></svg></div>
          <span class="help-row-num">03</span>
          <span class="help-row-text">The treatment record and COMPLETED status are saved in one transaction and cannot be duplicated.</span>
        </div>
      </section>
    </c:if>

    <%-- Clinic manager workflow --%>
    <c:if test="${sessionScope.authUser.role.name() eq 'CLINIC_MANAGER'}">
      <section class="card help-section">
        <div class="help-section-header">
          <div class="help-section-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none"
                 stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M3 3v18h18"/><path d="M18 17V9"/><path d="M13 17V5"/><path d="M8 17v-3"/>
            </svg>
          </div>
          <div>
            <h2>Clinic manager workflow</h2>
            <p>Steps specific to your manager profile</p>
          </div>
        </div>
        <div class="help-row">
          <div class="help-row-icon"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 3v18h18"/><path d="M18 17V9"/><path d="M13 17V5"/><path d="M8 17v-3"/></svg></div>
          <span class="help-row-num">01</span>
          <span class="help-row-text">Generate the daily appointment report for a date or billing summary for a range up to 366 days.</span>
        </div>
        <div class="help-row">
          <div class="help-row-icon"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg></div>
          <span class="help-row-num">02</span>
          <span class="help-row-text">Use User accounts to create staff logins, reset passwords, and activate or deactivate accounts. Dentist accounts receive a linked booking profile automatically.</span>
        </div>
        <div class="help-row">
          <div class="help-row-icon"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/></svg></div>
          <span class="help-row-num">03</span>
          <span class="help-row-text">Update treatment prices or deactivate treatments that must not appear in new bookings.</span>
        </div>
        <div class="help-row">
          <div class="help-row-icon"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="20" height="14" x="2" y="5" rx="2"/><line x1="2" x2="22" y1="10" y2="10"/></svg></div>
          <span class="help-row-num">04</span>
          <span class="help-row-text">Activating a new consultation fee closes the prior fee schedule while existing bills retain their original fee reference.</span>
        </div>
      </section>
    </c:if>

  </div><%-- /page-body --%>
</main>
<%@ include file="fragments/footer.jspf" %>
</body>
</html>
