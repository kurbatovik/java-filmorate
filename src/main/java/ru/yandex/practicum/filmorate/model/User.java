package ru.yandex.practicum.filmorate.model;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
@Builder
public class User {
    @Email(message = "Email is wrong")
    private final String email;
    @Positive(message = "ID cannot be negative")
    private int id;
    @NotBlank(message = "Login cannot be blank")
    private String login;
    @Builder.Default
    private String userName = login;
    @Past(message = "Birthdate cannot be in future or present")
    private LocalDate birthdate;
}
