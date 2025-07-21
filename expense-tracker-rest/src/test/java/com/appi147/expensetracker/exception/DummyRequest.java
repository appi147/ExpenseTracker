package com.appi147.expensetracker.exception;

import jakarta.validation.constraints.NotBlank;

public class DummyRequest {
    @NotBlank
    private String fieldName;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
