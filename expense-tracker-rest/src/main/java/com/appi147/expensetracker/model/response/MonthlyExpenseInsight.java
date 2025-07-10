package com.appi147.expensetracker.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyExpenseInsight {

    private BigDecimal monthlyBudget;
    private BigDecimal totalExpense;
    List<CategoryWiseExpense> categoryWiseExpenses = new ArrayList<>();
}
