package ru.yandex.practicum.filmorate.model.validator;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeParseException;

@Slf4j
public class MinDateValidator implements ConstraintValidator<MinDate, ChronoLocalDate> {

    LocalDate minDate;

    @Override
    public void initialize(MinDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        try {
            minDate = LocalDate.parse(constraintAnnotation.min());
        } catch (DateTimeParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isValid(ChronoLocalDate chronoLocalDate, ConstraintValidatorContext constraintValidatorContext) {
        return minDate.isBefore(chronoLocalDate);
    }
}
