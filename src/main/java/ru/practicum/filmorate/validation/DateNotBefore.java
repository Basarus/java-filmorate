package ru.practicum.filmorate.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateNotBeforeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateNotBefore {
    String message() default "date must be on or after {value}";
    String value();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}