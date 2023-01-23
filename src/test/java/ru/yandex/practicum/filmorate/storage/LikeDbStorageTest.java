package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/schema.sql", "/test-data.sql"})
class LikeDbStorageTest {

    private final LikeStorage storage;

    @Test
    void addAndGetLike() {
        storage.addLike(1, 1);
        assertThat(storage.getAll()).hasSize(1);
        assertThat(storage.getLikesByFilmId(1)).hasSize(1);

        storage.addLike(1, 1);
        assertThat(storage.getAll()).hasSize(1);
        assertThat(storage.getLikesByFilmId(1)).hasSize(1);

        storage.addLike(1, 2);
        assertThat(storage.getAll()).hasSize(2);
        assertThat(storage.getLikesByFilmId(1)).hasSize(2);
        assertThat(storage.getLikesByFilmId(1).get(0)).isEqualTo(1);
    }

    @Test
    void delLike() {
        storage.addLike(1, 1);
        storage.delLike(1, 1);
        assertThat(storage.getAll()).hasSize(0);
    }

    @Test
    void getPopular() {
        storage.addLike(3, 1);
        List<Long> filmsIds = storage.getPopular(10);
        assertThat(filmsIds).hasSize(3);
        assertThat(filmsIds.get(0)).isEqualTo(3);
        assertThat(filmsIds.get(1)).isEqualTo(1);
        assertThat(filmsIds.get(2)).isEqualTo(2);

        storage.addLike(3, 2);
        storage.addLike(2, 2);
        filmsIds = storage.getPopular(10);
        assertThat(filmsIds).hasSize(3);
        assertThat(filmsIds.get(0)).isEqualTo(3);
        assertThat(filmsIds.get(1)).isEqualTo(2);
        assertThat(filmsIds.get(2)).isEqualTo(1);

        storage.addLike(3, 2);
        storage.addLike(2, 2);
        filmsIds = storage.getPopular(10);
        assertThat(filmsIds).hasSize(3);
        assertThat(filmsIds.get(0)).isEqualTo(3);
        assertThat(filmsIds.get(1)).isEqualTo(2);
        assertThat(filmsIds.get(2)).isEqualTo(1);
    }

}