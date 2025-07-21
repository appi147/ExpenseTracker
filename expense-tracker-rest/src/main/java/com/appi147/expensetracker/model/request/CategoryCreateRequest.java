package com.appi147.expensetracker.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryCreateRequest {

    @NotBlank(message = "Label cannot be blank")
    @Size(max = 100, message = "Label must not exceed 100 characters")
    private String label;
}
