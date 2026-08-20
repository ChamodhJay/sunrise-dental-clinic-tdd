package com.icbt.controller;

import com.icbt.dao.DataAccessException;
import com.icbt.model.StaffUser;
import com.icbt.service.AuthenticationException;
import com.icbt.service.AuthenticationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;

@WebServlet("/login")
public final class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private transient AuthenticationService authenticationService;

    @Override
    public void init() {
        authenticationService = new AuthenticationService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (WebSupport.user(request) != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        char[] password = request.getParameter("password") == null
                ? new char[0] : request.getParameter("password").toCharArray();
        try {
            StaffUser user = authenticationService.authenticate(username, password, request.getRemoteAddr());
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession session = request.getSession(true);
            session.setMaxInactiveInterval(20 * 60);
            session.setAttribute(WebSupport.AUTH_USER, user.asSessionPrincipal());
            WebSupport.ensureCsrfToken(session);
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (AuthenticationException exception) {
            if (exception.getRetryAfterSeconds() > 0) {
                response.setHeader("Retry-After", String.valueOf(exception.getRetryAfterSeconds()));
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            request.setAttribute("loginError", exception.getMessage());
            request.setAttribute("submittedUsername", username);
            request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
        } catch (DataAccessException exception) {
            WebSupport.error(request, response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "The database is unavailable. Check the database setup and try again.");
        } finally {
            Arrays.fill(password, '\0');
        }
    }
}
