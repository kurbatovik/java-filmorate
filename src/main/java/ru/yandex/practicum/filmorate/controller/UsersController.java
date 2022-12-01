package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UsersController extends AbstractController {


    private final Map<String, User> users = new HashMap<>();

    @PostMapping("/users")
    public User postUser(@Validated @RequestBody User user) {
        String userEmail = user.getEmail();
        if (users.containsKey(userEmail)) {
            return users.get(userEmail);
        }

        user.setId(counter.incrementAndGet());
        users.put(userEmail, user);
        return user;
    }
}
