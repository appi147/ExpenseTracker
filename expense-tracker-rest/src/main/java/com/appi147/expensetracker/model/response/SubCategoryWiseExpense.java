package com.appi147.expensetracker.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubCategoryWiseExpense {

    private String subCategory;
    private BigDecimal amount;

}
