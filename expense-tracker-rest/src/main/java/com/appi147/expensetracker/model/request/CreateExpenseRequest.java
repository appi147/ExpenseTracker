package com.appi147.expensetracker.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateExpenseRequest {
    @NotNull
    private BigDecimal amount;

    @NotNull
    private LocalDate date;

    private String comments;

    @NotNull
    private Long subCategoryId;

    @NotNull
    private String paymentTypeCode;
}
