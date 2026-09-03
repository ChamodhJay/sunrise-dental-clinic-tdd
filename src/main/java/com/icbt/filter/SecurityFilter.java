package com.icbt.filter;

import com.icbt.controller.WebSupport;
import com.icbt.dao.DataAccessException;
import com.icbt.dao.StaffUserDAO;
import com.icbt.model.StaffRole;
import com.icbt.model.StaffUser;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;

@WebFilter("/*")
public final class SecurityFilter implements Filter {
    private final StaffUserDAO staffUserDAO;

    public SecurityFilter() {
        this(new StaffUserDAO());
    }

    SecurityFilter(StaffUserDAO staffUserDAO) {
        this.staffUserDAO = staffUserDAO;
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain chain) throws IOException, ServletException {
        if (!(servletRequest instanceof HttpServletRequest request)
                || !(servletResponse instanceof HttpServletResponse response)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("Referrer-Policy", "same-origin");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=()");
        if (request.isSecure()) {
            response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        }
        response.setHeader("Content-Security-Policy",
                "default-src 'self'; img-src 'self' data:; style-src 'self'; script-src 'self' 'unsafe-inline'");

        if ("TRACE".equalsIgnoreCase(request.getMethod())) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        String path = request.getServletPath();
        HttpSession session = request.getSession(false);
        if (path == null || path.isEmpty() || path.equals("/") || path.equals("/index.jsp")) {
            Object principal = session == null ? null : session.getAttribute(WebSupport.AUTH_USER);
            String destination = principal instanceof StaffUser ? "/dashboard" : "/login";
            response.sendRedirect(request.getContextPath() + destination);
            return;
        }
        if (isPublic(path)) {
            if ("/login".equals(path)) {
                if (session == null) {
                    session = request.getSession(true);
                }
                WebSupport.ensureCsrfToken(session);
                if (requiresCsrf(request) && !validCsrf(request, session)) {
                    csrfError(request, response);
                    return;
                }
            }
            chain.doFilter(request, response);
            return;
        }

        Object principal = session == null ? null : session.getAttribute(WebSupport.AUTH_USER);
        StaffUser sessionUser = principal instanceof StaffUser ? (StaffUser) principal : null;
        if (sessionUser == null || !sessionUser.isActive()) {
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        StaffUser user;
        try {
            Optional<StaffUser> currentUser = staffUserDAO.findById(sessionUser.getUserId());
            if (currentUser.isEmpty() || !currentUser.get().isActive()) {
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/login?accountDisabled=1");
                return;
            }
            user = currentUser.get();
            session.setAttribute(WebSupport.AUTH_USER, user.asSessionPrincipal());
        } catch (DataAccessException exception) {
            WebSupport.error(request, response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "The database is unavailable. Your request could not be authorized safely.");
            return;
        }

        WebSupport.ensureCsrfToken(session);
        if (requiresCsrf(request) && !validCsrf(request, session)) {
            csrfError(request, response);
            return;
        }

        StaffRole requiredRole = requiredRole(path);
        if (requiredRole != null && !user.hasRole(requiredRole)) {
            WebSupport.error(request, response, HttpServletResponse.SC_FORBIDDEN,
                    "Your account is not authorized to access this function.");
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean validCsrf(HttpServletRequest request, HttpSession session) {
        Object storedToken = session.getAttribute(WebSupport.CSRF_TOKEN);
        if (!(storedToken instanceof String)) {
            return false;
        }
        String expected = (String) storedToken;
        String supplied = request.getParameter("csrfToken");
        return expected != null && supplied != null && MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8), supplied.getBytes(StandardCharsets.UTF_8));
    }

    private boolean requiresCsrf(HttpServletRequest request) {
        String method = request.getMethod();
        return !("GET".equalsIgnoreCase(method)
                || "HEAD".equalsIgnoreCase(method)
                || "OPTIONS".equalsIgnoreCase(method));
    }

    private void csrfError(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        WebSupport.error(request, response, HttpServletResponse.SC_FORBIDDEN,
                "This form expired or could not be verified. Reload the page and try again.");
    }

    private boolean isPublic(String path) {
        return path.equals("/login")
                || path.startsWith("/assets/");
    }

    private StaffRole requiredRole(String path) {
        if (path.startsWith("/appointments") || path.startsWith("/billing")
                || path.startsWith("/api/appointments")) {
            return StaffRole.RECEPTIONIST;
        }
        if (path.startsWith("/dentist")) {
            return StaffRole.DENTIST;
        }
        if (path.startsWith("/reports") || path.startsWith("/catalogue")
                || path.startsWith("/users")) {
            return StaffRole.CLINIC_MANAGER;
        }
        return null;
    }

    @Override
    public void destroy() {
    }
}
