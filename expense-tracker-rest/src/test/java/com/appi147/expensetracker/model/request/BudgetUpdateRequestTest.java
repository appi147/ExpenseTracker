package com.appi147.expensetracker.model.request;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BudgetUpdateRequestTest extends ValidationTestBase {

    @Test
    void invalidAmount_shouldFailValidation() {
        BudgetUpdateRequest req = new BudgetUpdateRequest();
        req.setAmount(BigDecimal.ZERO); // invalid

        Set<ConstraintViolation<BudgetUpdateRequest>> violations = validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validAmount_shouldPass() {
        BudgetUpdateRequest req = new BudgetUpdateRequest();
        req.setAmount(new BigDecimal("100.00"));

        Set<ConstraintViolation<BudgetUpdateRequest>> violations = validate(req);
        assertTrue(violations.isEmpty());
    }
}

