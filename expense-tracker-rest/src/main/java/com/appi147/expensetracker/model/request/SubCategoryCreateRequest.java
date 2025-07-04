package com.appi147.expensetracker.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SubCategoryCreateRequest {

    @NotBlank
    @Size(max = 100)
    private String label;

    @NotNull
    private Long categoryId;
}
