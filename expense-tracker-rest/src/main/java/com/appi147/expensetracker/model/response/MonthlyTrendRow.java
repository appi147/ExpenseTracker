package com.appi147.expensetracker.model.response;

import java.math.BigDecimal;
import java.util.Map;

public record MonthlyTrendRow(String month, Map<String, BigDecimal> categoryAmounts) {
}
