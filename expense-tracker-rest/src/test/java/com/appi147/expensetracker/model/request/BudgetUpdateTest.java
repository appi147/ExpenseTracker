package com.appi147.expensetracker.model.request;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BudgetUpdateTest extends ValidationTestBase {

    @Test
    void invalidAmount_shouldFailValidation() {
        BudgetUpdate req = new BudgetUpdate();
        req.setAmount(BigDecimal.ZERO); // invalid

        Set<ConstraintViolation<BudgetUpdate>> violations = validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validAmount_shouldPass() {
        BudgetUpdate req = new BudgetUpdate();
        req.setAmount(new BigDecimal("100.00"));

        Set<ConstraintViolation<BudgetUpdate>> violations = validate(req);
        assertTrue(violations.isEmpty());
    }
}

