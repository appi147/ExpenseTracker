package com.appi147.expensetracker.model.request;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LabelUpdateRequestTest extends ValidationTestBase {

    @Test
    void blankLabel_shouldFail() {
        LabelUpdateRequest req = new LabelUpdateRequest();
        req.setLabel("   ");

        Set<ConstraintViolation<LabelUpdateRequest>> violations = validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validLabel_shouldPass() {
        LabelUpdateRequest req = new LabelUpdateRequest();
        req.setLabel("Transport");

        Set<ConstraintViolation<LabelUpdateRequest>> violations = validate(req);
        assertTrue(violations.isEmpty());
    }
}
