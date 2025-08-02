package com.appi147.expensetracker.model.request;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExpenseAmountPatchRequestTest extends ValidationTestBase {

    @Test
    void nullAmount_shouldFail() {
        ExpenseAmountPatchRequest req = new ExpenseAmountPatchRequest(null);

        Set<ConstraintViolation<ExpenseAmountPatchRequest>> violations = validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validAmount_shouldPass() {
        ExpenseAmountPatchRequest req = new ExpenseAmountPatchRequest(new BigDecimal("500.00"));

        Set<ConstraintViolation<ExpenseAmountPatchRequest>> violations = validate(req);
        assertTrue(violations.isEmpty());
    }
}

