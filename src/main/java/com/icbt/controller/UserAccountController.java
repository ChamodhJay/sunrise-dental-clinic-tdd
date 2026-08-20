package com.icbt.controller;

import com.icbt.dao.DataAccessException;
import com.icbt.service.BusinessRuleException;
import com.icbt.service.NotFoundException;
import com.icbt.service.UserAccountService;
import com.icbt.service.ValidationException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@WebServlet("/users")
public final class UserAccountController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private transient UserAccountService userAccountService;

    @Override
    public void init() {
        userAccountService = new UserAccountService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        show(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        char[] password = password(request, "password");
        char[] confirmation = password(request, "confirmPassword");
        try {
            String result;
            if ("create".equals(action)) {
                userAccountService.create(WebSupport.user(request), request.getParameter("username"),
                        request.getParameter("fullName"), password, confirmation,
                        request.getParameter("role"));
                result = "created";
            } else if ("resetPassword".equals(action)) {
                userAccountService.resetPassword(WebSupport.user(request),
                        request.getParameter("userId"), password, confirmation);
                result = "passwordReset";
            } else if ("activate".equals(action) || "deactivate".equals(action)) {
                userAccountService.setActive(WebSupport.user(request), request.getParameter("userId"),
                        "activate".equals(action));
                result = "statusChanged";
            } else {
                throw new BusinessRuleException("Select a valid account-management action.");
            }
            response.sendRedirect(request.getContextPath() + "/users?result=" + result);
        } catch (ValidationException exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("fieldErrors", exception.getFieldErrors());
            request.setAttribute("submittedAction", action);
            request.setAttribute("targetUserId", request.getParameter("userId"));
            show(request, response);
        } catch (NotFoundException exception) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            request.setAttribute("accountError", exception.getMessage());
            show(request, response);
        } catch (BusinessRuleException exception) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            request.setAttribute("accountError", exception.getMessage());
            show(request, response);
        } catch (SecurityException exception) {
            WebSupport.error(request, response, HttpServletResponse.SC_FORBIDDEN,
                    "Only the clinic manager can manage user accounts.");
        } catch (DataAccessException exception) {
            WebSupport.error(request, response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "User accounts are temporarily unavailable. No incomplete change was saved.");
        } finally {
            Arrays.fill(password, '\0');
            Arrays.fill(confirmation, '\0');
        }
    }

    private void show(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("users", userAccountService.findAll(WebSupport.user(request)));
            request.getRequestDispatcher("/WEB-INF/view/users.jsp").forward(request, response);
        } catch (SecurityException exception) {
            WebSupport.error(request, response, HttpServletResponse.SC_FORBIDDEN,
                    "Only the clinic manager can manage user accounts.");
        } catch (DataAccessException exception) {
            WebSupport.error(request, response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "User accounts are temporarily unavailable.");
        }
    }

    private char[] password(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? new char[0] : value.toCharArray();
    }
}
