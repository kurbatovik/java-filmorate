package ru.yandex.practicum.filmorate.model;


import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.validator.MinDate;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
public class Film implements ModelInterface {
    @PositiveOrZero(message = "ID cannot be negative")
    private final int id;
    @NotBlank(message = "Name can not be blank")
    private String name;
    @NotBlank(message = "Description can not be blank")
    @Size(max = 200, message = "Description can not be more 200 char")
    private String description;

    @NotNull(message = "Release date cannot be null")
    @MinDate(min = "1895-12-28", message = "Release date cannot be earlier than December 28, 1895")
    @PastOrPresent(message = "Release date can not be in future")
    private LocalDate releaseDate;

    @Positive(message = "Duration can not be negative or zero")
    private int duration;
}
