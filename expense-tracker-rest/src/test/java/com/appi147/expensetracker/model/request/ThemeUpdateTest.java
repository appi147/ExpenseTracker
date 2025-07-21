package com.appi147.expensetracker.model.request;

import com.appi147.expensetracker.enums.Theme;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThemeUpdateTest extends ValidationTestBase {

    @Test
    void nullTheme_shouldFail() {
        ThemeUpdate req = new ThemeUpdate();
        req.setTheme(null);

        Set<ConstraintViolation<ThemeUpdate>> violations = validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validTheme_shouldPass() {
        ThemeUpdate req = new ThemeUpdate();
        req.setTheme(Theme.DARK);

        Set<ConstraintViolation<ThemeUpdate>> violations = validate(req);
        assertTrue(violations.isEmpty());
    }
}
