package com.appi147.expensetracker.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SiteWideInsight {

    private long totalUsersRegistered;
    private long totalUsersAddedExpense;
    private BigDecimal totalExpensesAdded;
    private long totalTransactionsAdded;
    private long totalCategoriesCreated;
    private long totalSubCategoriesCreated;
}
