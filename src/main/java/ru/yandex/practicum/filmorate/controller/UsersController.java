package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Slf4j
@RequestMapping("/users")
@RestController
public class UsersController extends AbstractController {


    private final Map<Integer, User> users = new HashMap<>();
    private final Set<String> userEmails = new HashSet<>();

    @GetMapping("")
    public List<User> getUser() {
        log.info("User count: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(value = "")
    public User postUser(@Validated @RequestBody @NonNull User user) {
        log.debug("Request on create User: {}", user);
        String userEmail = user.getEmail();
        if (userEmails.contains(userEmail)) {
            String error = String.format("User with email: %s already exists", userEmail);
            log.info(error);
            throw new ResponseStatusException(UNPROCESSABLE_ENTITY,
                    error);
        }
        return createUser(user, userEmail);
    }

    @PutMapping(value = "")
    public User putUser(@Validated @RequestBody @NonNull User user) {
        return putUser(user, user.getId());
    }

    @PutMapping(value = "/{id}")
    public User putUser(@Validated @RequestBody @NonNull User user, @PathVariable(name = "id") int userID) {
        log.debug("Request on update User: {}", user);
        User updateUser = users.get(user.getId());
        if (updateUser == null) {
            String error = String.format("User with id: %d not found", userID);
            log.info(error);
            throw new ResponseStatusException(NOT_FOUND,
                    error);
        }
        log.debug("User has been found to update: {}", updateUser);
        updateUser.setEmail(user.getEmail());
        updateUser.setLogin(user.getLogin());
        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        updateUser.setBirthday(user.getBirthday());
        log.info("User updated: {}", updateUser);
        return updateUser;
    }

    private User createUser(@NonNull User user, String userEmail) {
        String userName = (user.getName() == null || user.getName().isBlank())
                ? user.getLogin() : user.getName();
        int userId = counter.incrementAndGet();
        User newUser = User.builder()
                .id(userId)
                .email(userEmail)
                .login(user.getLogin())
                .name(userName)
                .birthday(user.getBirthday())
                .build();
        users.put(userId, newUser);
        userEmails.add(userEmail);
        log.info("User has been created: {}", newUser);
        log.debug("User count: {}", users.size());
        return newUser;
    }
}
