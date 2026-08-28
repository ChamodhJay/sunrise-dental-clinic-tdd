<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <!doctype html>
    <html lang="en">

    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1">
      <title>Dashboard · Sunrise Dental Clinic</title>
      <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
    </head>

    <body>
      <%@ include file="fragments/header.jspf" %>
        <main class="page">

          <%-- Top bar --%>
            <div class="page-topbar">
              <div class="breadcrumb">
                <span>Sunrise Dental Clinic</span>
                <span class="bc-sep">›</span>
                <span class="bc-current">Overview</span>
              </div>
              <div class="page-topbar-right">
                <span class="status-dot"></span> Clinic open
              </div>
            </div>

            <%-- Body --%>
              <div class="page-body">

                <%-- Welcome --%>
                  <div class="welcome-block">
                    <div>
                      <div class="focus-label">Clinic Workspace</div>
                      <h2>Welcome back,
                        <c:out value="${sessionScope.authUser.fullName}" />
                      </h2>
                      <p>Here is what is happening at your clinic today.</p>
                    </div>
                    <div class="date-pill">Today session</div>
                  </div>

                  <%-- Focus card --%>
                    <div class="card focus-card">
                      <div class="focus-icon-box">
                        <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none"
                          stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                          <path d="M11 2v2" />
                          <path d="M5 2v2" />
                          <path d="M5 3H4a2 2 0 0 0-2 2v4a6 6 0 0 0 12 0V5a2 2 0 0 0-2-2h-1" />
                          <path d="M8 15v1a6 6 0 0 0 6 6v0a6 6 0 0 0 6-6v-4" />
                          <circle cx="20" cy="10" r="2" />
                        </svg>
                      </div>
                      <div class="focus-content">
                        <span class="focus-label">Today's Focus</span>
                        <h3>Keep every patient moving forward.</h3>
                        <p>Review your schedule, register new visits, and keep your team in sync.</p>
                      </div>
                      <div class="focus-stats">
                        <div class="date-pill"
                          style="border-color: var(--success); color: var(--success); display: inline-flex; align-items: center; gap: 0.5rem; font-weight: 600; font-size: 0.85rem;">
                          <span class="status-dot blink"></span> Active
                        </div>
                        <span style="display: block; margin-top: 0.4rem;">clinic session</span>
                      </div>
                    </div>

                    <%-- Action cards --%>
                      <div class="actions-grid">
                        <c:if test="${sessionScope.authUser.role.name() eq 'RECEPTIONIST'}">
                          <a class="action-card" href="${pageContext.request.contextPath}/appointments?action=new">
                            <div class="action-card-icon">
                              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24"
                                fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                stroke-linejoin="round">
                                <path d="M8 2v4" />
                                <path d="M16 2v4" />
                                <path d="M21 13V6a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h8" />
                                <path d="M3 10h18" />
                                <path d="M19 16v6" />
                                <path d="M16 19h6" />
                              </svg>
                            </div>
                            <span class="action-card-num"></span>
                            <strong>Register appointment</strong>
                            <span>Create the patient record, validate the slot, and generate an appointment
                              number.</span>
                            <span class="open-link">Open section <svg xmlns="http://www.w3.org/2000/svg" width="13"
                                height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
                                stroke-linecap="round" stroke-linejoin="round">
                                <path d="M5 12h14" />
                                <path d="m12 5 7 7-7 7" />
                              </svg></span>
                          </a>
                          <a class="action-card" href="${pageContext.request.contextPath}/appointments">
                            <div class="action-card-icon">
                              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24"
                                fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                stroke-linejoin="round">
                                <circle cx="11" cy="11" r="8" />
                                <path d="m21 21-4.3-4.3" />
                              </svg>
                            </div>
                            <span class="action-card-num"></span>
                            <strong>View appointment details</strong>
                            <span>Search by the unique appointment number.</span>
                            <span class="open-link">Open section <svg xmlns="http://www.w3.org/2000/svg" width="13"
                                height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
                                stroke-linecap="round" stroke-linejoin="round">
                                <path d="M5 12h14" />
                                <path d="m12 5 7 7-7 7" />
                              </svg></span>
                          </a>
                          <a class="action-card" href="${pageContext.request.contextPath}/billing">
                            <div class="action-card-icon">
                              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24"
                                fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                stroke-linejoin="round">
                                <rect width="20" height="14" x="2" y="5" rx="2" />
                                <line x1="2" x2="22" y1="10" y2="10" />
                              </svg>
                            </div>
                            <span class="action-card-num"></span>
                            <strong>Calculate and print bill</strong>
                            <span>Generate the two-item receipt after treatment is completed.</span>
                            <span class="open-link">Open section <svg xmlns="http://www.w3.org/2000/svg" width="13"
                                height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
                                stroke-linecap="round" stroke-linejoin="round">
                                <path d="M5 12h14" />
                                <path d="m12 5 7 7-7 7" />
                              </svg></span>
                          </a>
                        </c:if>

                        <c:if test="${sessionScope.authUser.role.name() eq 'DENTIST'}">
                          <a class="action-card" href="${pageContext.request.contextPath}/dentist/appointments">
                            <div class="action-card-icon">
                              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24"
                                fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                stroke-linejoin="round">
                                <path d="M8 2v4" />
                                <path d="M16 2v4" />
                                <rect width="18" height="18" x="3" y="4" rx="2" />
                                <path d="M3 10h18" />
                                <path d="M8 14h.01" />
                                <path d="M12 14h.01" />
                                <path d="M16 14h.01" />
                              </svg>
                            </div>
                            <span class="action-card-num"></span>
                            <strong>Assigned appointments</strong>
                            <span>View assigned patient details and record completed treatment.</span>
                            <span class="open-link">Open section <svg xmlns="http://www.w3.org/2000/svg" width="13"
                                height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
                                stroke-linecap="round" stroke-linejoin="round">
                                <path d="M5 12h14" />
                                <path d="m12 5 7 7-7 7" />
                              </svg></span>
                          </a>
                        </c:if>

                        <c:if test="${sessionScope.authUser.role.name() eq 'CLINIC_MANAGER'}">
                          <a class="action-card" href="${pageContext.request.contextPath}/reports">
                            <div class="action-card-icon">
                              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24"
                                fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                stroke-linejoin="round">
                                <path d="M3 3v18h18" />
                                <path d="M18 17V9" />
                                <path d="M13 17V5" />
                                <path d="M8 17v-3" />
                              </svg>
                            </div>
                            <span class="action-card-num"></span>
                            <strong>Operational reports</strong>
                            <span>View daily appointment and billing summary reports.</span>
                            <span class="open-link">Open section <svg xmlns="http://www.w3.org/2000/svg" width="13"
                                height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
                                stroke-linecap="round" stroke-linejoin="round">
                                <path d="M5 12h14" />
                                <path d="m12 5 7 7-7 7" />
                              </svg></span>
                          </a>
                          <a class="action-card" href="${pageContext.request.contextPath}/catalogue">
                            <div class="action-card-icon">
                              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24"
                                fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                stroke-linejoin="round">
                                <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20" />
                              </svg>
                            </div>
                            <span class="action-card-num"></span>
                            <strong>Treatment catalogue and fees</strong>
                            <span>Maintain treatment prices and the active consultation fee.</span>
                            <span class="open-link">Open section <svg xmlns="http://www.w3.org/2000/svg" width="13"
                                height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
                                stroke-linecap="round" stroke-linejoin="round">
                                <path d="M5 12h14" />
                                <path d="m12 5 7 7-7 7" />
                              </svg></span>
                          </a>
                          <a class="action-card" href="${pageContext.request.contextPath}/users">
                            <div class="action-card-icon">
                              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24"
                                fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                stroke-linejoin="round">
                                <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
                                <circle cx="9" cy="7" r="4" />
                                <path d="M22 21v-2a4 4 0 0 0-3-3.87" />
                                <path d="M16 3.13a4 4 0 0 1 0 7.75" />
                              </svg>
                            </div>
                            <span class="action-card-num"></span>
                            <strong>User account management</strong>
                            <span>Create staff accounts, reset passwords, and activate or deactivate access.</span>
                            <span class="open-link">Open section <svg xmlns="http://www.w3.org/2000/svg" width="13"
                                height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
                                stroke-linecap="round" stroke-linejoin="round">
                                <path d="M5 12h14" />
                                <path d="m12 5 7 7-7 7" />
                              </svg></span>
                          </a>
                        </c:if>

                        <a class="action-card" href="${pageContext.request.contextPath}/help">
                          <div class="action-card-icon">
                            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24"
                              fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                              stroke-linejoin="round">
                              <circle cx="12" cy="12" r="10" />
                              <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3" />
                              <path d="M12 17h.01" />
                            </svg>
                          </div>
                          <span class="action-card-num">
                            <c:choose>
                              <c:when test="${sessionScope.authUser.role.name() eq 'RECEPTIONIST'}"></c:when>
                              <c:when test="${sessionScope.authUser.role.name() eq 'DENTIST'}"></c:when>
                              <c:otherwise></c:otherwise>
                            </c:choose>
                          </span>
                          <strong>Help instructions</strong>
                          <span>Read role-specific workflow and validation guidance.</span>
                          <span class="open-link">Open section <svg xmlns="http://www.w3.org/2000/svg" width="13"
                              height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
                              stroke-linecap="round" stroke-linejoin="round">
                              <path d="M5 12h14" />
                              <path d="m12 5 7 7-7 7" />
                            </svg></span>
                        </a>
                      </div>

                      <%-- Stats --%>
                        <div class="stat-grid">
                          <div class="stat-card">
                            <h4>Today's appointments</h4>
                            <div class="value">${dashboardStats.todayAppointmentCount}</div>
                            <div class="desc">Scheduled for today</div>
                          </div>
                          <div class="stat-card">
                            <h4>Available dentists</h4>
                            <div class="value">${dashboardStats.activeDentistCount}</div>
                            <div class="desc">Booking enabled</div>
                          </div>
                          <div class="stat-card">
                            <h4>Completed visits</h4>
                            <div class="value">${dashboardStats.completedVisitsThisMonth}</div>
                            <div class="desc">This month</div>
                          </div>
                        </div>

              </div><%-- /page-body --%>
        </main>
        <%@ include file="fragments/footer.jspf" %>
    </body>

    </html>