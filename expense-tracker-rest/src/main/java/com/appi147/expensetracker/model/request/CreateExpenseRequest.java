package com.appi147.expensetracker.model.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateExpenseRequest {

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than 0")
    @Digits(integer = 19, fraction = 2, message = "Amount format is invalid (max 10 digits, 2 decimal places)")
    private BigDecimal amount;

    @NotNull(message = "Date cannot be null")
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate date;

    @Size(max = 500, message = "Comments must not exceed 500 characters")
    private String comments;

    @NotNull(message = "SubCategoryId cannot be null")
    @Positive(message = "SubCategoryId must be positive")
    private Long subCategoryId;

    @NotNull(message = "PaymentTypeCode cannot be null")
    @Pattern(regexp = "^[A-Z_]+$", message = "PaymentTypeCode must be uppercase and may contain underscores only")
    private String paymentTypeCode;
}
