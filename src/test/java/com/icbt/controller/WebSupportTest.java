package com.icbt.controller;

import com.icbt.model.StaffRole;
import com.icbt.model.StaffUser;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WebSupportTest {
    @Test
    public void malformedSessionPrincipalIsTreatedAsAnonymous() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(WebSupport.AUTH_USER)).thenReturn("not-a-user");

        assertNull(WebSupport.user(request));
    }

    @Test
    public void validSessionPrincipalIsReturned() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        StaffUser user = new StaffUser(UUID.randomUUID(), "manager", "", "Manager",
                StaffRole.CLINIC_MANAGER, true, LocalDateTime.of(2026, 9, 3, 9, 0));
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(WebSupport.AUTH_USER)).thenReturn(user);

        assertEquals(user, WebSupport.user(request));
    }

    @Test
    public void malformedCsrfTokenIsReplaced() {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(WebSupport.CSRF_TOKEN)).thenReturn(12345);

        String token = WebSupport.ensureCsrfToken(session);

        assertFalse(token.isBlank());
        verify(session).setAttribute(eq(WebSupport.CSRF_TOKEN), eq(token));
    }
}
