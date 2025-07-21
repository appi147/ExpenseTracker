package com.appi147.expensetracker.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SubCategoryCreateRequest {

    @NotBlank(message = "Label cannot be blank")
    @Size(max = 100, message = "Label must not exceed 100 characters")
    private String label;

    @NotNull(message = "Category ID cannot be null")
    @Positive(message = "Category ID must be a positive number")
    private Long categoryId;
}
