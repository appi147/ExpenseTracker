package com.appi147.expensetracker.model.response;

import java.math.BigDecimal;

public record MonthlyExpense(BigDecimal last30Days, BigDecimal currentMonth) {
}
