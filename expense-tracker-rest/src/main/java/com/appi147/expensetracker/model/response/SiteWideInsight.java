package com.appi147.expensetracker.model.response;

import java.math.BigDecimal;

public record SiteWideInsight(long totalUsersRegistered, long totalUsersAddedExpense, BigDecimal totalExpensesAdded,
                              long totalTransactionsAdded, long totalCategoriesCreated,
                              long totalSubCategoriesCreated) {

}
