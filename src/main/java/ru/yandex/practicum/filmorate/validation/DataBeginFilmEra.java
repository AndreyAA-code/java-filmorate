package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.METHOD, ElementType.FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = DataBeginFilmEraValidator.class)

public @interface DataBeginFilmEra {

    String message() default "Ошибка в дате релиза фильма";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}