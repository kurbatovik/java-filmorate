package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/schema.sql", "/test-data.sql"})
class UserDbStorageTest {

    private final UserDbStorage userStorage;

    @Test
    void create() {
        User user = getUser();
        assertThat(userStorage.getAll()).hasSize(3);
        user = userStorage.create(user);
        assertThat(userStorage.getAll()).hasSize(4);
        assertThat(user.getId()).isEqualTo(4);
    }

    @Test
    void update() {
        User user = userStorage.getById(1).orElseThrow();
        user.setName("Tolka");
        userStorage.update(user.getId(), user);
        assertThat(userStorage.getById(1).orElseThrow().getName()).isEqualTo("Tolka");

        user.setLogin("Tolka");
        userStorage.update(user.getId(), user);
        assertThat(userStorage.getById(1).orElseThrow().getLogin()).isEqualTo("Tolka");

        user.setEmail("Tolka@gmail.com");
        userStorage.update(user.getId(), user);
        assertThat(userStorage.getById(1).orElseThrow().getEmail()).isEqualTo("Tolka@gmail.com");

        user.setBirthday(LocalDate.now().minusYears(33));
        userStorage.update(user.getId(), user);
        assertThat(userStorage.getById(1).orElseThrow().getBirthday())
                .isEqualTo(LocalDate.now().minusYears(33));
    }

    @Test
    void delete() {
        assertThat(userStorage.getAll()).hasSize(3);
        assertThat(userStorage.delete(1)).isTrue();
        assertThat(userStorage.getAll()).hasSize(2);
        assertThat(userStorage.delete(22)).isFalse();
        assertThat(userStorage.getAll()).hasSize(2);
    }

    @Test
    void getAll() {
        List<User> users = userStorage.getAll();
        assertThat(users).hasSize(3);
        assertThat(users.get(0).getId()).isEqualTo(1);
        assertThat(users.get(0).getName()).isEqualTo("est adipisicing");
    }

    @Test
    void getById() {
        User user = userStorage.getById(1).orElseThrow();
        assertThat(user.getName()).isEqualTo("est adipisicing");
    }

    @Test
    void getByIdSet() {
        List<Long> idsUsers = List.of(3L, 1L, 2L);
        List<User> users = userStorage.getByIdSet(idsUsers);
        assertThat(users).hasSize(3);
        User user = users.get(0);
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getName()).isEqualTo("est adipisicing");
    }


    private static User getUser() {
        return User.builder()
                .name("Heroku")
                .login("test")
                .email("heroku@email.com")
                .birthday(LocalDate.EPOCH)
                .build();
    }
}