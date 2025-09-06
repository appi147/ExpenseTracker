package com.appi147.expensetracker.model.request;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RecurringExpenseCreateRequestTest extends ValidationTestBase {

    @Test
    void invalidFields_shouldFailValidation() {
        RecurringExpenseCreateRequest req = new RecurringExpenseCreateRequest();
        req.setAmount(new BigDecimal("-10")); // invalid: < 0.01
        req.setComments("a".repeat(501)); // invalid: exceeds 500 chars
        req.setDayOfMonth(0); // invalid: < 1
        req.setSubCategoryId(-5L); // invalid: negative
        req.setPaymentTypeCode("lowercase123"); // invalid pattern

        Set<ConstraintViolation<RecurringExpenseCreateRequest>> violations = validate(req);
        assertFalse(violations.isEmpty());
        assertEquals(5, violations.size()); // exactly 5 violations
    }

    @Test
    void validFields_shouldPassValidation() {
        RecurringExpenseCreateRequest req = new RecurringExpenseCreateRequest();
        req.setAmount(new BigDecimal("150.50"));
        req.setComments("Monthly subscription");
        req.setDayOfMonth(15);
        req.setSubCategoryId(2L);
        req.setPaymentTypeCode("CREDIT_CARD");

        Set<ConstraintViolation<RecurringExpenseCreateRequest>> violations = validate(req);
        assertTrue(violations.isEmpty());
    }

    @Test
    void boundaryDays_shouldPassValidation() {
        RecurringExpenseCreateRequest req = new RecurringExpenseCreateRequest();
        req.setAmount(new BigDecimal("50.00"));
        req.setComments("Test boundary days");
        req.setSubCategoryId(1L);
        req.setPaymentTypeCode("UPI");

        // Test day = 1
        req.setDayOfMonth(1);
        Set<ConstraintViolation<RecurringExpenseCreateRequest>> violations1 = validate(req);
        assertTrue(violations1.isEmpty());

        // Test day = 28
        req.setDayOfMonth(28);
        Set<ConstraintViolation<RecurringExpenseCreateRequest>> violations28 = validate(req);
        assertTrue(violations28.isEmpty());
    }

    @Test
    void amount_withMaxDigitsAndTwoDecimals_shouldPassValidation() {
        RecurringExpenseCreateRequest req = new RecurringExpenseCreateRequest();
        req.setAmount(new BigDecimal("12345678901234567.89")); // 17 integer digits, 2 decimals
        req.setComments("Test amount format");
        req.setDayOfMonth(10);
        req.setSubCategoryId(3L);
        req.setPaymentTypeCode("CARD");

        Set<ConstraintViolation<RecurringExpenseCreateRequest>> violations = validate(req);
        assertTrue(violations.isEmpty());
    }

    @Test
    void amount_tooManyDecimals_shouldFailValidation() {
        RecurringExpenseCreateRequest req = new RecurringExpenseCreateRequest();
        req.setAmount(new BigDecimal("100.123")); // 3 decimals
        req.setComments("Invalid decimals");
        req.setDayOfMonth(5);
        req.setSubCategoryId(2L);
        req.setPaymentTypeCode("UPI");

        Set<ConstraintViolation<RecurringExpenseCreateRequest>> violations = validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("amount")));
    }
}
