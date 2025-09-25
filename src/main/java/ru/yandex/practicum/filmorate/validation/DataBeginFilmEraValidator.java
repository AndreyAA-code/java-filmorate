package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DataBeginFilmEraValidator implements ConstraintValidator<DataBeginFilmEra, LocalDate> {

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date != null) {
            return date.isAfter(LocalDate.of(1895, 12, 28));
        }
        return false;
    }

}