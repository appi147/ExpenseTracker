package com.appi147.expensetracker.model.request;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryCreateRequestTest extends ValidationTestBase {

    @Test
    void blankLabel_shouldFail() {
        CategoryCreateRequest req = new CategoryCreateRequest();
        req.setLabel("  "); // blank

        Set<ConstraintViolation<CategoryCreateRequest>> violations = validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validLabel_shouldPass() {
        CategoryCreateRequest req = new CategoryCreateRequest();
        req.setLabel("Groceries");

        Set<ConstraintViolation<CategoryCreateRequest>> violations = validate(req);
        assertTrue(violations.isEmpty());
    }
}

