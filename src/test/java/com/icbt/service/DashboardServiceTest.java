package com.icbt.service;

import com.icbt.dao.AppointmentDAO;
import com.icbt.dao.ReferenceDataDAO;
import com.icbt.model.DashboardStats;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DashboardServiceTest {

    private AppointmentDAO appointmentDAO;
    private ReferenceDataDAO referenceDataDAO;
    private DashboardService dashboardService;

    private static final LocalDate TODAY = LocalDate.of(2026, 8, 26);

    @Before
    public void setUp() {
        appointmentDAO    = mock(AppointmentDAO.class);
        referenceDataDAO  = mock(ReferenceDataDAO.class);
        dashboardService  = new DashboardService(appointmentDAO, referenceDataDAO);
    }

    @Test
    public void loadStatsReturnsTodayAppointmentCountFromDAO() {
        when(appointmentDAO.countForDate(TODAY)).thenReturn(7);
        when(referenceDataDAO.countActiveDentists()).thenReturn(3);
        when(appointmentDAO.countCompletedThisMonth(TODAY)).thenReturn(15);

        DashboardStats stats = dashboardService.loadStats(TODAY);

        assertEquals(7, stats.getTodayAppointmentCount());
        verify(appointmentDAO).countForDate(TODAY);
    }

    @Test
    public void loadStatsReturnsActiveDentistCountFromDAO() {
        when(appointmentDAO.countForDate(TODAY)).thenReturn(0);
        when(referenceDataDAO.countActiveDentists()).thenReturn(4);
        when(appointmentDAO.countCompletedThisMonth(TODAY)).thenReturn(0);

        DashboardStats stats = dashboardService.loadStats(TODAY);

        assertEquals(4, stats.getActiveDentistCount());
        verify(referenceDataDAO).countActiveDentists();
    }

    @Test
    public void loadStatsReturnsCompletedThisMonthCountFromDAO() {
        when(appointmentDAO.countForDate(TODAY)).thenReturn(0);
        when(referenceDataDAO.countActiveDentists()).thenReturn(0);
        when(appointmentDAO.countCompletedThisMonth(TODAY)).thenReturn(22);

        DashboardStats stats = dashboardService.loadStats(TODAY);

        assertEquals(22, stats.getCompletedVisitsThisMonth());
        verify(appointmentDAO).countCompletedThisMonth(TODAY);
    }

    @Test
    public void loadStatsCombinesAllThreeCountsIntoSingleObject() {
        when(appointmentDAO.countForDate(TODAY)).thenReturn(5);
        when(referenceDataDAO.countActiveDentists()).thenReturn(2);
        when(appointmentDAO.countCompletedThisMonth(TODAY)).thenReturn(10);

        DashboardStats stats = dashboardService.loadStats(TODAY);

        assertEquals(5,  stats.getTodayAppointmentCount());
        assertEquals(2,  stats.getActiveDentistCount());
        assertEquals(10, stats.getCompletedVisitsThisMonth());
    }

    @Test
    public void loadStatsReturnsZerosWhenDatabaseHasNoData() {
        when(appointmentDAO.countForDate(TODAY)).thenReturn(0);
        when(referenceDataDAO.countActiveDentists()).thenReturn(0);
        when(appointmentDAO.countCompletedThisMonth(TODAY)).thenReturn(0);

        DashboardStats stats = dashboardService.loadStats(TODAY);

        assertEquals(0, stats.getTodayAppointmentCount());
        assertEquals(0, stats.getActiveDentistCount());
        assertEquals(0, stats.getCompletedVisitsThisMonth());
    }
}
