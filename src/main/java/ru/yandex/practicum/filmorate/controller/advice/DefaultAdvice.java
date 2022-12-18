package ru.yandex.practicum.filmorate.controller.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.controller.dto.Responce;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class DefaultAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Responce> handleException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            log.info("Validation failed for field: {}. Error message: {}", fieldName, errorMessage);
        });
        Responce responce = Responce.builder().message("Validation failed").errors(errors).build();
        return new ResponseEntity<>(responce, HttpStatus.BAD_REQUEST);
    }
}
