package com.icbt.controller;

import com.icbt.model.StaffUser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

public final class WebSupport {
    public static final String AUTH_USER = "authUser";
    public static final String CSRF_TOKEN = "csrfToken";

    private WebSupport() { }

    public static StaffUser user(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (StaffUser) session.getAttribute(AUTH_USER);
    }

    public static String ensureCsrfToken(HttpSession session) {
        String token = (String) session.getAttribute(CSRF_TOKEN);
        if (token == null) {
            byte[] random = new byte[32];
            new SecureRandom().nextBytes(random);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(random);
            session.setAttribute(CSRF_TOKEN, token);
        }
        return token;
    }

    public static void error(HttpServletRequest request, HttpServletResponse response,
                             int status, String message) throws ServletException, IOException {
        response.setStatus(status);
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher("/WEB-INF/view/error.jsp").forward(request, response);
    }
}
