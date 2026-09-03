package com.icbt.controller;

import com.icbt.dao.DataAccessException;
import com.icbt.model.Bill;
import com.icbt.service.BillingService;
import com.icbt.service.BusinessRuleException;
import com.icbt.service.NotFoundException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/billing")
public final class BillingController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private transient BillingService billingService;

    public BillingController() {
    }

    BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @Override
    public void init() {
        if (billingService == null) {
            billingService = new BillingService();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String appointmentNumber = request.getParameter("appointmentNumber");
        if (appointmentNumber == null || appointmentNumber.isBlank()) {
            request.getRequestDispatcher("/WEB-INF/view/billing.jsp").forward(request, response);
            return;
        }
        try {
            request.setAttribute("bill", billingService.findBill(appointmentNumber));
            request.getRequestDispatcher("/WEB-INF/view/billing.jsp").forward(request, response);
        } catch (NotFoundException exception) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            request.setAttribute("billingError", exception.getMessage());
            request.getRequestDispatcher("/WEB-INF/view/billing.jsp").forward(request, response);
        } catch (BusinessRuleException exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("billingError", exception.getMessage());
            request.getRequestDispatcher("/WEB-INF/view/billing.jsp").forward(request, response);
        } catch (DataAccessException exception) {
            databaseError(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String appointmentNumber = request.getParameter("appointmentNumber");
        String action = request.getParameter("action");
        if (!("create".equals(action) || "print".equals(action))) {
            badRequest(request, response, "Select a valid billing action.");
            return;
        }
        if (appointmentNumber == null || appointmentNumber.isBlank()) {
            badRequest(request, response, "Appointment number is required.");
            return;
        }
        try {
            if ("print".equals(action)) {
                Bill bill = billingService.markPrinted(appointmentNumber, WebSupport.user(request));
                request.setAttribute("bill", bill);
                request.setAttribute("printNow", true);
                request.getRequestDispatcher("/WEB-INF/view/billing.jsp").forward(request, response);
                return;
            }
            Bill bill = billingService.calculateBill(appointmentNumber, WebSupport.user(request));
            String encoded = URLEncoder.encode(bill.getAppointment().getAppointmentNumber(), StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/billing?created=1&appointmentNumber=" + encoded);
        } catch (NotFoundException exception) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            request.setAttribute("billingError", exception.getMessage());
            request.getRequestDispatcher("/WEB-INF/view/billing.jsp").forward(request, response);
        } catch (BusinessRuleException exception) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            request.setAttribute("billingError", exception.getMessage());
            request.getRequestDispatcher("/WEB-INF/view/billing.jsp").forward(request, response);
        } catch (SecurityException exception) {
            WebSupport.error(request, response, HttpServletResponse.SC_FORBIDDEN,
                    "Only a receptionist can generate or print patient bills.");
        } catch (DataAccessException exception) {
            databaseError(request, response);
        }
    }

    private void databaseError(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        WebSupport.error(request, response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                "Billing data is temporarily unavailable. No incomplete bill was saved.");
    }

    private void badRequest(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        request.setAttribute("billingError", message);
        request.getRequestDispatcher("/WEB-INF/view/billing.jsp").forward(request, response);
    }
}
