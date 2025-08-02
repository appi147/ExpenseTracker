package com.appi147.expensetracker.model.request;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentTypeUpsertRequestTest extends ValidationTestBase {

    @Test
    void invalidCode_shouldFail() {
        PaymentTypeUpsertRequest req = new PaymentTypeUpsertRequest();
        req.setCode("pay@type");
        req.setLabel("Pay Type");

        Set<ConstraintViolation<PaymentTypeUpsertRequest>> violations = validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validCodeAndLabel_shouldPass() {
        PaymentTypeUpsertRequest req = new PaymentTypeUpsertRequest();
        req.setCode("CREDIT_CARD");
        req.setLabel("Credit Card");

        Set<ConstraintViolation<PaymentTypeUpsertRequest>> violations = validate(req);
        assertTrue(violations.isEmpty());
    }
}
