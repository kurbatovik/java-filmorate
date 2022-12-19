package ru.yandex.practicum.filmorate.model.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinDateValidator.class)
@Documented
public @interface MinDate {

    String message() default "Early date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String min() default "-999999999-01-01";
}
