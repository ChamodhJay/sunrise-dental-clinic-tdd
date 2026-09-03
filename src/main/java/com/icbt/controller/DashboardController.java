package com.icbt.controller;

import com.icbt.dao.DataAccessException;
import com.icbt.model.DashboardStats;
import com.icbt.service.DashboardService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/dashboard")
public final class DashboardController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private transient DashboardService dashboardService;

    public DashboardController() {
    }

    DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Override
    public void init() {
        if (dashboardService == null) {
            dashboardService = new DashboardService();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            DashboardStats stats = dashboardService.loadStats(LocalDate.now());
            request.setAttribute("dashboardStats", stats);
            request.getRequestDispatcher("/WEB-INF/view/dashboard.jsp").forward(request, response);
        } catch (DataAccessException exception) {
            WebSupport.error(request, response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Dashboard data is temporarily unavailable. Try again shortly.");
        }
    }
}
