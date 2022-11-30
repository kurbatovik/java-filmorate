package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController extends AbstractController {


    private Map<String, User> users = new HashMap<>();

    @PostMapping("/user")
    public User postUser(@Validated @RequestBody User user) {
        String userEmail = user.getEmail();
        if (users.containsKey(userEmail)) {
            return users.get(userEmail);
        }
        User newUser = User.builder()
                .id(counter.incrementAndGet())
                .email(userEmail)
                .login(user.getLogin())
                .userName(user.getUserName().isBlank()? userEmail : user.getUserName())
                .birthdate(user.getBirthdate())
                .build();
        user.setId(counter.incrementAndGet());
        return user;
    }
}
