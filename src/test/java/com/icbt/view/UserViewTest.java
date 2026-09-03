package com.icbt.view;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserViewTest {
    private static final Path USERS_VIEW =
            Path.of("src/main/webapp/WEB-INF/view/users.jsp");

    @Test
    public void passwordResetDialogCanBeOpenedByItsCssClass() throws IOException {
        String view = Files.readString(USERS_VIEW);

        assertFalse(view.contains("style=\"display: none;\""));
        assertTrue(view.contains("id=\"resetModal\" class=\"modal-overlay\" role=\"dialog\""));
    }

    @Test
    public void passwordFieldsUseUniqueIds() throws IOException {
        String view = Files.readString(USERS_VIEW);

        assertEquals(1, count(view, "id=\"confirmPassword\""));
        assertEquals(1, count(view, "id=\"resetConfirmPassword\""));
    }

    @Test
    public void failedPasswordResetReopensTheDialog() throws IOException {
        String view = Files.readString(USERS_VIEW);

        assertTrue(view.contains("submittedAction eq 'resetPassword'"));
        assertTrue(view.contains("openResetModal(document.getElementById('resetModalUserId').value)"));
    }

    private int count(String source, String value) {
        return source.split(java.util.regex.Pattern.quote(value), -1).length - 1;
    }
}
