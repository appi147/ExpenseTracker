package com.appi147.expensetracker.model.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetUpdate {
    private BigDecimal amount;
}
