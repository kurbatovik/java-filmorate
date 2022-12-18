package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Slf4j
@RequestMapping("/users")
@RestController
public class UsersController extends AbstractController<User> {

    private final Set<String> userEmails = new HashSet<>();

    public UsersController() {
        entityName = "User";
    }

    @Override
    protected User create(User user) {
        String userName = (user.getName() == null || user.getName().isBlank())
                ? user.getLogin() : user.getName();
        String userEmail = user.getEmail();
        if (userEmails.contains(userEmail)) {
            String error = String.format("User with email: %s already exists", userEmail);
            log.info(error);
            throw new ResponseStatusException(UNPROCESSABLE_ENTITY,
                    error);
        }
        User newUser = User.builder()
                .id(counter.incrementAndGet())
                .email(userEmail)
                .login(user.getLogin())
                .name(userName)
                .birthday(user.getBirthday())
                .build();
        userEmails.add(userEmail);
        return newUser;
    }

    @Override
    public User update(int userID, User user, User updateUser) {
        if (!user.getEmail().equals(updateUser.getEmail())) {
            userEmails.remove(user.getEmail());
            userEmails.add(updateUser.getEmail());
        }
        updateUser.setEmail(user.getEmail());
        updateUser.setLogin(user.getLogin());
        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        updateUser.setBirthday(user.getBirthday());
        return updateUser;
    }
}
