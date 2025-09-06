package com.appi147.expensetracker.model.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class RecurringExpenseCreateRequest {

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than 0")
    @Digits(integer = 19, fraction = 2, message = "Amount format is invalid (max 10 digits, 2 decimal places)")
    private BigDecimal amount;

    @Size(max = 500, message = "Comments must not exceed 500 characters")
    private String comments;

    @NotNull
    @Min(value = 1, message = "Day of month must be at least 1")
    @Max(value = 28, message = "Day of month must not be greater than 28")
    private int dayOfMonth;

    @NotNull(message = "SubCategoryId cannot be null")
    @Positive(message = "SubCategoryId must be positive")
    private Long subCategoryId;

    @NotNull(message = "PaymentTypeCode cannot be null")
    @Pattern(regexp = "^[A-Z_]+$", message = "PaymentTypeCode must be uppercase and may contain underscores only")
    private String paymentTypeCode;
}
