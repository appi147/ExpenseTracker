package com.appi147.expensetracker.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LabelUpdateRequest {

    @NotBlank
    @Size(max = 100)
    private String label;
}
