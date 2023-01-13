package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class ErrorResponse {

    private final Map<String, String> errors;
    private String message;
}
