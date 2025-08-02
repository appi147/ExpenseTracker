package com.appi147.expensetracker.model.request;

import com.appi147.expensetracker.enums.Theme;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThemeUpdateRequestTest extends ValidationTestBase {

    @Test
    void nullTheme_shouldFail() {
        ThemeUpdateRequest req = new ThemeUpdateRequest();
        req.setTheme(null);

        Set<ConstraintViolation<ThemeUpdateRequest>> violations = validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validTheme_shouldPass() {
        ThemeUpdateRequest req = new ThemeUpdateRequest();
        req.setTheme(Theme.DARK);

        Set<ConstraintViolation<ThemeUpdateRequest>> violations = validate(req);
        assertTrue(violations.isEmpty());
    }
}
