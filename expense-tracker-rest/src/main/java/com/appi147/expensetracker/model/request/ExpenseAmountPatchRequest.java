package com.appi147.expensetracker.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExpenseAmountPatchRequest {

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 19, fraction = 2, message = "Amount format is invalid (max 10 digits, 2 decimal places)")
    private BigDecimal amount;
}
