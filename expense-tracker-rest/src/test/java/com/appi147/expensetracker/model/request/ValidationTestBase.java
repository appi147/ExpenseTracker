package com.appi147.expensetracker.model.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

public abstract class ValidationTestBase {
    protected final Validator validator;

    protected ValidationTestBase() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    protected <T> Set<ConstraintViolation<T>> validate(T obj) {
        return validator.validate(obj);
    }
}
