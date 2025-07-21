package com.appi147.expensetracker.model.response;

import java.math.BigDecimal;
import java.util.List;

public record CategoryWiseExpense(String category, BigDecimal amount,
                                  List<SubCategoryWiseExpense> subCategoryWiseExpenses) {
}
