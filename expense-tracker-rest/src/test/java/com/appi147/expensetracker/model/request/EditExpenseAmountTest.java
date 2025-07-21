package com.appi147.expensetracker.model.request;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EditExpenseAmountTest extends ValidationTestBase {

    @Test
    void nullAmount_shouldFail() {
        EditExpenseAmount req = new EditExpenseAmount(null);

        Set<ConstraintViolation<EditExpenseAmount>> violations = validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validAmount_shouldPass() {
        EditExpenseAmount req = new EditExpenseAmount(new BigDecimal("500.00"));

        Set<ConstraintViolation<EditExpenseAmount>> violations = validate(req);
        assertTrue(violations.isEmpty());
    }
}

