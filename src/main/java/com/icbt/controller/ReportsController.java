package com.icbt.controller;

import com.icbt.dao.DataAccessException;
import com.icbt.service.BusinessRuleException;
import com.icbt.service.ReportService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@WebServlet("/reports")
public final class ReportsController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private transient ReportService reportService;

    @Override
    public void init() {
        reportService = new ReportService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String type = request.getParameter("type");
        try {
            if ("daily".equals(type)) {
                LocalDate date = parseDate(request.getParameter("date"), LocalDate.now());
                request.setAttribute("reportDate", date);
                request.setAttribute("dailyAppointments",
                        reportService.dailyAppointments(WebSupport.user(request), date));
            } else if ("billing".equals(type)) {
                LocalDate to = parseDate(request.getParameter("to"), LocalDate.now());
                LocalDate from = parseDate(request.getParameter("from"), to.minusDays(30));
                request.setAttribute("fromDate", from);
                request.setAttribute("toDate", to);
                request.setAttribute("billingSummary",
                        reportService.billingSummary(WebSupport.user(request), from, to));
            }
            request.setAttribute("today", LocalDate.now());
            request.getRequestDispatcher("/WEB-INF/view/reports.jsp").forward(request, response);
        } catch (BusinessRuleException | DateTimeParseException exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("reportError", exception instanceof DateTimeParseException
                    ? "Enter valid report dates." : exception.getMessage());
            request.setAttribute("today", LocalDate.now());
            request.getRequestDispatcher("/WEB-INF/view/reports.jsp").forward(request, response);
        } catch (DataAccessException exception) {
            WebSupport.error(request, response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "The selected report could not be generated because the database is unavailable.");
        }
    }

    private LocalDate parseDate(String value, LocalDate defaultValue) {
        return value == null || value.isBlank() ? defaultValue : LocalDate.parse(value);
    }
}
