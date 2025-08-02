package com.appi147.expensetracker.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PaymentTypeUpsertRequest {

    @NotBlank(message = "Code cannot be blank")
    @Size(max = 50, message = "Code must not exceed 50 characters")
    @Pattern(regexp = "^[A-Z_]+$", message = "Code must be uppercase letters and underscores only")
    private String code;

    @NotBlank(message = "Label cannot be blank")
    @Size(max = 100, message = "Label must not exceed 100 characters")
    private String label;
}
