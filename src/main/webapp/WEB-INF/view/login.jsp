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
      <div class="login-image-section">
        <div class="image-overlay-content">
          <div class="brand-top">
            <div class="logo-circle">
              <svg width="36" height="36" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">

                <!-- Sunrise -->
                <path d="M8 17a10 10 0 0 1 20 0" stroke="currentColor" stroke-width="2" stroke-linecap="round" />

                <!-- Sun rays -->
                <path d="M18 4v4" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
                <path d="M10 7l2.5 2.5" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
                <path d="M26 7l-2.5 2.5" stroke="currentColor" stroke-width="2" stroke-linecap="round" />

                <!-- Tooth -->
                <path d="M12.5 15.5
                     C10.5 13.5 8 14.5 8.5 18
                     C9 21.5 10.5 24 11.5 27
                     C12 29 13 30 14 29
                     C15 28 15 25 18 25
                     C21 25 21 28 22 29
                     C23 30 24 29 24.5 27
                     C25.5 24 27 21.5 27.5 18
                     C28 14.5 25.5 13.5 23.5 15.5
                     C22 17 20.5 17.5 18 17.5
                     C15.5 17.5 14 17 12.5 15.5Z" fill="none" stroke="currentColor" stroke-width="2"
                  stroke-linejoin="round" />

                <!-- S -->
                <path d="M20.8 19
                     C20.2 18.4 19.3 18.2 18.5 18.5
                     C17.5 18.8 17.5 20 18.5 20.4
                     L20 21
                     C21 21.4 21 22.6 20 23
                     C19.1 23.4 18.1 23.2 17.5 22.6" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />

              </svg>
            </div>
            <div class="brand-text">
              <strong>Sunrise Dental Clinic</strong>
              <small>Appointment & patient management</small>
            </div>
          </div>

          <div class="hero-text">
            <h2>Care, connected from the moment they arrive.</h2>
            <p>One simple, secure workspace for your receptionists, dentists, and clinic managers<br>
              so your clinic can run smoothly and your patients always come first.
            </p>
          </div>

          <div class="feature-list">
            <div class="feature-item">
              <div class="icon-circle"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                  stroke-width="2">
                  <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                  <line x1="16" y1="2" x2="16" y2="6"></line>
                  <line x1="8" y1="2" x2="8" y2="6"></line>
                  <line x1="3" y1="10" x2="21" y2="10"></line>
                </svg></div>
              <div class="feature-text">
                <strong>Smart scheduling</strong>
                <span>Manage appointments, availability, and daily schedules with ease.</span>
              </div>
            </div>
            <div class="feature-item">
              <div class="icon-circle"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                  stroke-width="2">
                  <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                  <circle cx="9" cy="7" r="4"></circle>
                  <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                  <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                </svg></div>
              <div class="feature-text">
                <strong>Unified patient records</strong>
                <span>Keep patient profiles, medical history, treatment notes, and records all in one place.</span>
              </div>
            </div>
            <div class="feature-item">
              <div class="icon-circle"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                  stroke-width="2">
                  <circle cx="12" cy="12" r="10"></circle>
                  <polyline points="12 6 12 12 16 14"></polyline>
                </svg></div>
              <div class="feature-text">
                <strong>Real-time collaboration</strong>
                <span>Keep your entire team updated with instant access to the information they need.</span>
              </div>
            </div>

            <div class="feature-item">

              <div class="icon-circle">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M12 3L20 6V11C20 16.2 16.6 19.8 12 21C7.4 19.8 4 16.2 4 11V6L12 3Z"></path>
                  <path d="M9 12L11 14L15 10"></path>
                </svg>
              </div>

              <div class="feature-text">
                <strong>Secure &amp; reliable</strong>
                <span>Protect patient information with secure access and role-based permissions.</span>
              </div>

            </div>

          </div>

          <div class="copyright">&copy; 2026 Sunrise Dental Clinic. All rights reserved.</div>
        </div>
      </div>

      <div class="login-form-section">
        <main class="login-card">
          <div class="secure-badge">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
              <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
            </svg>
            Secure staff access
          </div>

          <h1>Staff login</h1>
          <p class="subtitle">Sign in to manage appointments and patient records for Sunrise Dental Clinic.</p>

          <c:if test="${param.loggedOut eq '1'}">
            <div class="alert success" role="status">You have exited the system safely.</div>
          </c:if>
          <c:if test="${param.accountDisabled eq '1'}">
            <div class="alert error" role="alert">This account is inactive. Contact the clinic manager.</div>
          </c:if>
          <c:if test="${not empty loginError}">
            <div class="alert error" role="alert">
              <c:out value="${loginError}" />
            </div>
          </c:if>

          <form action="${pageContext.request.contextPath}/login" method="post">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

            <div class="field">
              <label for="username">Username</label>
              <div class="input-wrapper">
                <svg class="input-icon left-icon" width="16" height="16" viewBox="0 0 24 24" fill="none"
                  stroke="currentColor" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                  <circle cx="12" cy="7" r="4"></circle>
                </svg>
                <input id="username" name="username" type="text" maxlength="50"
                  value="<c:out value='${submittedUsername}' />" autocomplete="username" required autofocus>
              </div>
            </div>

            <div class="field">
              <div class="password-label-row">
                <label for="password">Password</label>
                <!-- <a href="#" class="forgot-link">Forgot password?</a> -->
              </div>
              <div class="input-wrapper">
                <svg class="input-icon left-icon" width="16" height="16" viewBox="0 0 24 24" fill="none"
                  stroke="currentColor" stroke-width="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                </svg>
                <input id="password" name="password" type="password" maxlength="128" autocomplete="current-password"
                  required>
                <button id="togglePassword" class="password-toggle" type="button" aria-controls="password"
                  aria-label="Show password" aria-pressed="false">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                    stroke-width="2" aria-hidden="true">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                    <circle cx="12" cy="12" r="3"></circle>
                  </svg>
                </button>
              </div>
            </div>

            <button class="button" type="submit">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
              </svg>
              Log in securely
            </button>
          </form>

          <p class="footer-note">Protected system. Access is monitored and restricted to authorized Sunrise Dental
            Clinic staff.</p>
        </main>
      </div>
      <script>
        const password = document.getElementById('password');
        const togglePassword = document.getElementById('togglePassword');
        togglePassword.addEventListener('click', function () {
          const showPassword = password.type === 'password';
          password.type = showPassword ? 'text' : 'password';
          togglePassword.setAttribute('aria-pressed', String(showPassword));
          togglePassword.setAttribute('aria-label', showPassword ? 'Hide password' : 'Show password');
        });
      </script>
    </body>

    </html>
