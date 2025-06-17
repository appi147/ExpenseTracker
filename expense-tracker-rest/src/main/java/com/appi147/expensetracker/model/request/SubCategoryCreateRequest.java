package com.appi147.expensetracker.model.request;

import lombok.Data;

@Data
public class SubCategoryCreateRequest {
    private String label;
    private Long categoryId;
}
