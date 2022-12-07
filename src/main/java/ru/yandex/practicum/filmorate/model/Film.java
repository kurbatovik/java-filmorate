package ru.yandex.practicum.filmorate.model;


import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.model.validator.MinDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    @PositiveOrZero(message = "ID cannot be negative")
    @NonNull
    private final int id;
    @NotBlank(message = "Name can not be blank")
    @NonNull
    private String name;
    @NotBlank(message = "Description can not be blank")
    @Size(max = 200, message = "Description can not be more 200 char")
    @NonNull
    private String description;

    @NonNull
    @MinDate(min = "1895-12-28")
    private LocalDate releaseDate;

    @NonNull
    @Positive(message = "Duration can't be negative or zero")
    private int duration;


}
