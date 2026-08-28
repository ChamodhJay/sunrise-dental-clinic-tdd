package com.icbt.controller;

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
    private transient DashboardService dashboardService = new DashboardService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DashboardStats stats = dashboardService.loadStats(LocalDate.now());
        request.setAttribute("dashboardStats", stats);
        request.getRequestDispatcher("/WEB-INF/view/dashboard.jsp").forward(request, response);
    }
}
