package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.sql.Date;
import java.time.LocalDate;

public class DataBeginFilmEraValidator implements ConstraintValidator<DataBeginFilmEra, Date> {

    @Override
    public boolean isValid(Date date, ConstraintValidatorContext context) {
        if (date != null) {
            return date.after(Date.valueOf(LocalDate.of(1895, 12, 28)));
        }
       return false;
    }

}
