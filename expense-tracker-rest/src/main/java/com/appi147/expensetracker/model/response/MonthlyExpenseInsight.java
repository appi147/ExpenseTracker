package com.appi147.expensetracker.model.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

public record MonthlyExpenseInsight(
        @Schema(description = "Total monthly budget set by user", example = "10000.00")
        BigDecimal monthlyBudget,

        @Schema(description = "Total expenses for the month", example = "7650.50")
        BigDecimal totalExpense,

        @Schema(description = "Breakdown of expenses category-wise")
        List<CategoryWiseExpense> categoryWiseExpenses
) {
}
