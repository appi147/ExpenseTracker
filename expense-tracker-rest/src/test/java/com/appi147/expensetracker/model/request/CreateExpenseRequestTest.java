package com.appi147.expensetracker.model.request;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateExpenseRequestTest extends ValidationTestBase {

    @Test
    void invalidFields_shouldFail() {
        CreateExpenseRequest req = new CreateExpenseRequest();
        req.setAmount(null); // invalid
        req.setDate(LocalDate.now().plusDays(1)); // future date
        req.setSubCategoryId(null);
        req.setPaymentTypeCode("invalid code!"); // fails regex

        Set<ConstraintViolation<CreateExpenseRequest>> violations = validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validFields_shouldPass() {
        CreateExpenseRequest req = new CreateExpenseRequest();
        req.setAmount(new BigDecimal("20.00"));
        req.setDate(LocalDate.now());
        req.setComments("Lunch");
        req.setSubCategoryId(1L);
        req.setPaymentTypeCode("UPI");

        Set<ConstraintViolation<CreateExpenseRequest>> violations = validate(req);
        assertTrue(violations.isEmpty());
    }
}

