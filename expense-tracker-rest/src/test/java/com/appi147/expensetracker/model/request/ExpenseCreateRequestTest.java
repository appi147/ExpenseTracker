package com.appi147.expensetracker.model.request;

import com.appi147.expensetracker.enums.AmortizationPeriod;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExpenseCreateRequestTest extends ValidationTestBase {

    @Test
    void invalidFields_shouldFail() {
        ExpenseCreateRequest req = new ExpenseCreateRequest();
        req.setAmount(null); // invalid
        req.setDate(LocalDate.now().plusDays(1)); // future date
        req.setSubCategoryId(null);
        req.setPaymentTypeCode("invalid code!"); // fails regex
        req.setMonthsToAmortize(null); // missing amortization

        Set<ConstraintViolation<ExpenseCreateRequest>> violations = validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validFields_shouldPass() {
        ExpenseCreateRequest req = new ExpenseCreateRequest();
        req.setAmount(new BigDecimal("20.00"));
        req.setDate(LocalDate.now());
        req.setComments("Lunch");
        req.setSubCategoryId(1L);
        req.setPaymentTypeCode("UPI");
        req.setMonthsToAmortize(AmortizationPeriod.ONE_MONTH); // ✅ now required

        Set<ConstraintViolation<ExpenseCreateRequest>> violations = validate(req);
        assertTrue(violations.isEmpty());
    }
}
