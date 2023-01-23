package ru.yandex.practicum.filmorate.controller.advice;

import lombok.extern.slf4j.Slf4j;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestControllerAdvice
public class DefaultAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            log.info("Validation failed for field: {}. Error message: {}", fieldName, errorMessage);
        });
        return ErrorResponse.builder().message("Validation failed").errors(errors).build();
    }

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(NumberFormatException ex) {
        return ErrorResponse.builder().message(ex.getMessage()).build();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleException(NotFoundException ex) {
        return ErrorResponse.builder().message(ex.getMessage()).build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        violations.forEach(error -> {
            String propertyPath = error.getPropertyPath().toString();
            String message = error.getMessage();
            errors.put(propertyPath, message);
            log.info("Validation failed for property: {}. Error message: {}", propertyPath, message);
        });
        return ErrorResponse.builder().message(ex.getMessage()).errors(errors).build();
    }

    @ExceptionHandler(JdbcSQLIntegrityConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(JdbcSQLIntegrityConstraintViolationException ex) {
        ErrorResponse error = ErrorResponse.builder().message(ex.getOriginalMessage()).build();
        log.info("Violation of SQL integrity constraint: {}.", ex.getOriginalMessage());
        log.debug(ex.getSQL());
        return error;
    }
}
