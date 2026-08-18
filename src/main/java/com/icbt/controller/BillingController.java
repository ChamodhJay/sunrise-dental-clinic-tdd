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
    private BillingService billingService;

    @Override
    public void init() {
        billingService = new BillingService();
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
        } catch (NotFoundException | BusinessRuleException exception) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
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
        try {
            if ("print".equals(request.getParameter("action"))) {
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
        } catch (DataAccessException exception) {
            databaseError(request, response);
        }
    }

    private void databaseError(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        WebSupport.error(request, response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                "Billing data is temporarily unavailable. No incomplete bill was saved.");
    }
}
