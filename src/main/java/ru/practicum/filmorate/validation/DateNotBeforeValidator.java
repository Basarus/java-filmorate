package ru.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateNotBeforeValidator implements ConstraintValidator<DateNotBefore, LocalDate> {
    private LocalDate min;

    @Override
    public void initialize(DateNotBefore constraintAnnotation) {
        this.min = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return !value.isBefore(min);
    }
}
