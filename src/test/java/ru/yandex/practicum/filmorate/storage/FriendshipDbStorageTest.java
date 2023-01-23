package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/schema.sql", "/test-data.sql"})
class FriendshipDbStorageTest {

    private final FriendshipStorage storage;

    @Test
    void addFriend() {
        assertThat(storage.getAll()).hasSize(0);
        storage.addFriend(1, 2);
        assertThat(storage.getAll()).hasSize(1);
        assertThat(storage.findFriendsByUserId(1)).hasSize(1);

        storage.addFriend(1, 2);
        assertThat(storage.getAll()).hasSize(1);
        assertThat(storage.findFriendsByUserId(1)).hasSize(1);

        storage.addFriend(3, 1);
        assertThat(storage.getAll()).hasSize(2);
        assertThat(storage.findFriendsByUserId(1)).hasSize(1);

        storage.addFriend(1, 3);
        assertThat(storage.getAll()).hasSize(2);
        assertThat(storage.findFriendsByUserId(1)).hasSize(2);

        storage.addFriend(2, 1);
        assertThat(storage.getAll()).hasSize(2);
        assertThat(storage.findFriendsByUserId(1)).hasSize(2);
        assertThat(storage.findFriendsByUserId(2)).hasSize(1);
    }

    @Test
    void delFriend() {
        storage.addFriend(1, 2);
        storage.addFriend(2, 1);
        storage.delFriend(1, 2);
    }

}