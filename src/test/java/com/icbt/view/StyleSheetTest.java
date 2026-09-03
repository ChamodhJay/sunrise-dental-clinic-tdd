package com.icbt.view;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class StyleSheetTest {
    private static final Path STYLE_SHEET = Path.of("src/main/webapp/assets/app.css");
    private static final Pattern VARIABLE_DEFINITION = Pattern.compile("--([a-z0-9-]+)\\s*:");
    private static final Pattern VARIABLE_USE = Pattern.compile("var\\(--([a-z0-9-]+)\\)");

    @Test
    public void everyCssVariableHasADefinition() throws IOException {
        String css = Files.readString(STYLE_SHEET);
        Set<String> definitions = matches(css, VARIABLE_DEFINITION);
        Set<String> missing = matches(css, VARIABLE_USE);
        missing.removeAll(definitions);

        assertTrue("Undefined CSS variables: " + missing, missing.isEmpty());
    }

    @Test
    public void keyboardControlsHaveVisibleFocusStyles() throws IOException {
        String css = Files.readString(STYLE_SHEET);

        assertTrue(css.contains(".button:focus-visible"));
        assertTrue(css.contains(".password-toggle:focus-visible"));
        assertTrue(css.contains("outline: 3px solid"));
    }

    private Set<String> matches(String source, Pattern pattern) {
        Set<String> values = new HashSet<>();
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            values.add(matcher.group(1));
        }
        return values;
    }
}
