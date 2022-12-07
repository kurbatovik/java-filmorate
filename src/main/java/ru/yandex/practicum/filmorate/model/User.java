package ru.yandex.practicum.filmorate.model;


import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

@Data
@Builder
public class User {
    @PositiveOrZero(message = "ID cannot be negative")
    @NonNull
    private final int id;
    @Pattern(regexp = "^[A-Z0-9._%+-]+@[A-Z0-9-]+(.[A-Z0-9-]+)*\\.[A-Z]{2,}$", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Email is wrong")
    @NonNull
    private String email;
    @NotBlank(message = "Login can not be blank")
    @Pattern(regexp = "^\\S+?", message = "Login can not contains spaces")
    @NonNull
    private String login;

    private String name;
    @NonNull
    @Past(message = "Birthdate cannot be in future or present")
    private LocalDate birthday;


}
