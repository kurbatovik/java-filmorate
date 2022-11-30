package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Data
@Builder
public class User {
    @Positive(message = "ID cannot be negative")
    private int id;
    @Email(message = "Email is wrong")
    private final String email;
    @NotBlank(message = "Login cannot be blank")
    private String login;
    @Builder.Default
    private String userName = login;
    @Past(message = "Birthdate cannot be in future or present")
    private LocalDate birthdate;
}
