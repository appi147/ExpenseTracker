package com.appi147.expensetracker.model.request;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubCategoryCreateRequestTest extends ValidationTestBase {

    @Test
    void missingFields_shouldFail() {
        SubCategoryCreateRequest req = new SubCategoryCreateRequest();
        req.setLabel("");
        req.setCategoryId(null);

        Set<ConstraintViolation<SubCategoryCreateRequest>> violations = validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validFields_shouldPass() {
        SubCategoryCreateRequest req = new SubCategoryCreateRequest();
        req.setLabel("Snacks");
        req.setCategoryId(10L);

        Set<ConstraintViolation<SubCategoryCreateRequest>> violations = validate(req);
        assertTrue(violations.isEmpty());
    }
}
