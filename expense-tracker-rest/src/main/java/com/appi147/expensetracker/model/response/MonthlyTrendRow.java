package com.appi147.expensetracker.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyTrendRow {
    private String month;
    private Map<String, BigDecimal> categoryAmounts = new LinkedHashMap<>();

    public void addCategoryAmount(String category, BigDecimal amount) {
        this.categoryAmounts.put(category, amount);
    }
}
