package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Data
@Builder
@EqualsAndHashCode()
public class Genre implements Model {

    @PositiveOrZero(message = "ID cannot be negative")
    private long id;

    @NotBlank(message = "Name can not be blank")
    private String name;

}
