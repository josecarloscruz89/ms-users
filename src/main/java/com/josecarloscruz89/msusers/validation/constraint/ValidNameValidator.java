package com.josecarloscruz89.msusers.validation.constraint;

import com.josecarloscruz89.msusers.validation.annotation.ValidName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidNameValidator implements ConstraintValidator<ValidName, String> {

    @Override
    public boolean isValid(String fieldName, ConstraintValidatorContext constraintValidatorContext) {
        return fieldName == null || !fieldName.matches(".*[0-9].*");
    }
}