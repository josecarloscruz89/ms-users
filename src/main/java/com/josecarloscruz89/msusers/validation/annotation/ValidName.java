package com.josecarloscruz89.msusers.validation.annotation;

import com.josecarloscruz89.msusers.validation.constraint.ValidNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidNameValidator.class)
public @interface ValidName {

    String message() default "The field name shouldn't accept numbers";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}