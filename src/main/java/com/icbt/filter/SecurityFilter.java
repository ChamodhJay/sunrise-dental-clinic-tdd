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
    private final StaffUserDAO staffUserDAO = new StaffUserDAO();

    @Override
    public void init(FilterConfig filterConfig) { }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("Referrer-Policy", "same-origin");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Content-Security-Policy",
                "default-src 'self'; img-src 'self' data:; style-src 'self'; script-src 'self' 'unsafe-inline'");

        HttpSession session = request.getSession(true);
        WebSupport.ensureCsrfToken(session);
        if ("POST".equalsIgnoreCase(request.getMethod()) && !validCsrf(request, session)) {
            WebSupport.error(request, response, HttpServletResponse.SC_FORBIDDEN,
                    "This form expired or could not be verified. Reload the page and try again.");
            return;
        }

        String path = request.getServletPath();
        if (isPublic(path)) {
            chain.doFilter(request, response);
            return;
        }

        StaffUser sessionUser = (StaffUser) session.getAttribute(WebSupport.AUTH_USER);
        if (sessionUser == null || !sessionUser.isActive()) {
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
            session.setAttribute(WebSupport.AUTH_USER, user);
        } catch (DataAccessException exception) {
            WebSupport.error(request, response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "The database is unavailable. Your request could not be authorized safely.");
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
        String expected = (String) session.getAttribute(WebSupport.CSRF_TOKEN);
        String supplied = request.getParameter("csrfToken");
        return expected != null && supplied != null && MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8), supplied.getBytes(StandardCharsets.UTF_8));
    }

    private boolean isPublic(String path) {
        return path.equals("/") || path.equals("/index.jsp") || path.equals("/login")
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
    public void destroy() { }
}
