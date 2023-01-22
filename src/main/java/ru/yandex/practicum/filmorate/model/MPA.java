package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Data
@Builder
public class MPA implements Model{

    @PositiveOrZero(message = "ID cannot be negative")
    private final long id;

    @NotBlank(message = "Name can not be blank")
    private String name;
}
