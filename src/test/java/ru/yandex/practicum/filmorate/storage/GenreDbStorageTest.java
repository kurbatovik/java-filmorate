package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/schema.sql", "/test-data.sql"})
class GenreDbStorageTest {

    private final GenreDbStorage genreStorage;

    @Test
    void create() {
        Genre genre = getGenre();
        assertThat(genreStorage.getAll()).hasSize(6);
        genre = genreStorage.create(genre);
        assertThat(genreStorage.getAll()).hasSize(7);
        assertThat(genre.getId()).isEqualTo(7);
    }

    @Test
    void update() {
        Genre genre = genreStorage.getById(1).get();
        genre.setName("Приключения");
        genreStorage.update(genre.getId(), genre);
        assertThat(genreStorage.getById(1).get().getName()).isEqualTo("Приключения");
    }

    @Test
    void delete() {
        assertThat(genreStorage.getAll()).hasSize(6);
        assertThat(genreStorage.delete(5)).isTrue();
        assertThat(genreStorage.getAll()).hasSize(5);
        assertThat(genreStorage.delete(22)).isFalse();
        assertThat(genreStorage.getAll()).hasSize(5);
    }

    @Test
    void getAll() {
        List<Genre> genres = genreStorage.getAll();
        assertThat(genres).hasSize(6);
        assertThat(genres.get(0).getId()).isEqualTo(1);
        assertThat(genres.get(0).getName()).isEqualTo("Комедия");
    }

    @Test
    void getById() {
        Genre genre = genreStorage.getById(1).orElse(null);
        assertThat(genre.getName()).isEqualTo("Комедия");
        genre = genreStorage.getById(9).orElse(null);
        assertThat(genre).isNull();
    }

    @Test
    void getByIdSet() {
        List<Long> idsGenres = List.of(3L, 1L, 2L);
        List<Genre> genres = genreStorage.getByIdSet(idsGenres);
        assertThat(genres).hasSize(3);
        Genre genre = genres.get(0);
        assertThat(genre.getId()).isEqualTo(1);
        assertThat(genre.getName()).isEqualTo("Комедия");
    }
    

    private static Genre getGenre(){
        return Genre.builder()
                .name("GG-21")
                .build();
    }
}