package ru.yandex.practicum.filmorate.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class Responce {

    private final Map<String, String> errors;
    private String message;
}
