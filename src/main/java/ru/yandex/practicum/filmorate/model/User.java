package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
@Builder
public class User implements Model {

    @PositiveOrZero(message = "ID cannot be negative")
    private final long id;
    private final Set<Long> friends = new HashSet<>();
    private final Set<Long> likedFilms = new HashSet<>();
    @Pattern(regexp = "^[A-Z0-9._%+-]+@[A-Z0-9-]+(.[A-Z0-9-]+)*\\.[A-Z]{2,}$", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Email is wrong")
    @NotNull(message = "Email can not be null")
    private String email;
    @Pattern(regexp = "^\\S+?", message = "Login can not be blank and contains spaces")
    @NotNull(message = "Login can not be null")
    private String login;
    private String name;
    @NotNull(message = "Birthdate can not be null")
    @Past(message = "Birthdate can not be in future or present")
    private LocalDate birthday;
}
