package com.appi147.expensetracker.model.response;

import java.math.BigDecimal;
import java.util.List;

public record MonthlyExpenseInsight(BigDecimal monthlyBudget, BigDecimal totalExpense,
                                    List<CategoryWiseExpense> categoryWiseExpenses) {

}
