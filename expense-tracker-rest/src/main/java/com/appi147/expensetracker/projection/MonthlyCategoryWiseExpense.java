package com.appi147.expensetracker.projection;

import java.math.BigDecimal;

public interface MonthlyCategoryWiseExpense {
    String getMonth();
    String getCategory();
    BigDecimal getTotalAmount();
}
