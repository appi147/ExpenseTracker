package com.appi147.expensetracker.model.request;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentTypeRequestTest extends ValidationTestBase {

    @Test
    void invalidCode_shouldFail() {
        PaymentTypeRequest req = new PaymentTypeRequest();
        req.setCode("pay@type");
        req.setLabel("Pay Type");

        Set<ConstraintViolation<PaymentTypeRequest>> violations = validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validCodeAndLabel_shouldPass() {
        PaymentTypeRequest req = new PaymentTypeRequest();
        req.setCode("CREDIT_CARD");
        req.setLabel("Credit Card");

        Set<ConstraintViolation<PaymentTypeRequest>> violations = validate(req);
        assertTrue(violations.isEmpty());
    }
}
