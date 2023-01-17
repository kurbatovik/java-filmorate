package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;


@Slf4j
@Component
public class InMemoryUserStorage extends InMemoryStorage<User> {

    @Override
    public User create(User user) {
        String userName = (user.getName() == null || user.getName().isBlank())
                ? user.getLogin() : user.getName();
        User newUser = User.builder()
                .id(getNextId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(userName)
                .birthday(user.getBirthday())
                .build();
        put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public boolean delete(long id) {
        return remove(id);
    }

}
