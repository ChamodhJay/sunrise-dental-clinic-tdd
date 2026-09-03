package com.icbt.view;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoginViewTest {
    private static final Path LOGIN_VIEW =
            Path.of("src/main/webapp/WEB-INF/view/login.jsp");

    @Test
    public void passwordButtonChangesPasswordVisibility() throws IOException {
        String view = Files.readString(LOGIN_VIEW);

        assertTrue(view.contains("id=\"togglePassword\""));
        assertTrue(view.contains("password.type = showPassword ? 'text' : 'password'"));
        assertTrue(view.contains("aria-label=\"Show password\""));
    }

    @Test
    public void loginDoesNotOfferUnsupportedPersistentSession() throws IOException {
        String view = Files.readString(LOGIN_VIEW);

        assertFalse(view.contains("Keep me signed in"));
        assertFalse(view.contains("id=\"keep-signed\""));
    }

    @Test
    public void loginPageListsOnlySupportedStaffRoles() throws IOException {
        String view = Files.readString(LOGIN_VIEW);

        assertTrue(view.contains("receptionists, dentists, and clinic managers"));
        assertFalse(view.contains("nurses, hygienists"));
    }
}
