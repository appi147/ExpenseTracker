package com.appi147.expensetracker.model.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MonthlyExpense {

    private BigDecimal last30Days;
    private BigDecimal currentMonth;
}
