package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/schema.sql", "/test-data.sql"})
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;

    @Test
    void create() {
        Film film = getFilm();
        assertThat(filmStorage.getAll()).hasSize(3);
        film = filmStorage.create(film);
        assertThat(filmStorage.getAll()).hasSize(4);
        assertThat(film.getId()).isEqualTo(4);
    }

    @Test
    void update() {
        Film film = filmStorage.getById(1);
        film.setName("Tolka");
        filmStorage.update(film.getId(), film);
        assertThat(filmStorage.getById(1).getName()).isEqualTo("Tolka");

        film.setDescription("Tolka");
        filmStorage.update(film.getId(), film);
        assertThat(filmStorage.getById(1).getDescription()).isEqualTo("Tolka");

        film.setDuration(50);
        filmStorage.update(film.getId(), film);
        assertThat(filmStorage.getById(1).getDuration()).isEqualTo(50);

        film.setReleaseDate(LocalDate.now().minusYears(33));
        filmStorage.update(film.getId(), film);
        assertThat(filmStorage.getById(1).getReleaseDate())
                .isEqualTo(LocalDate.now().minusYears(33));

        assertThat(filmStorage.getById(1).getMpa().getId()).isEqualTo(3);
        film.setMpa(MPA.builder().id(1).build());
        filmStorage.update(film.getId(), film);
        assertThat(filmStorage.getById(1).getMpa().getId()).isEqualTo(1);

        assertThat(filmStorage.getById(1).getGenres()).hasSize(0);
        film.getGenres().add(Genre.builder().id(2).build());
        film.getGenres().add(Genre.builder().id(1).build());
        filmStorage.update(film.getId(), film);
        assertThat(filmStorage.getById(1).getGenres()).hasSize(2);
        assertThat(filmStorage.getById(1).getGenres().get(0).getId()).isEqualTo(2);
        assertThat(filmStorage.getById(1).getGenres().get(1).getId()).isEqualTo(1);
    }

    @Test
    void delete() {
        assertThat(filmStorage.getAll()).hasSize(3);
        assertThat(filmStorage.delete(1)).isTrue();
        assertThat(filmStorage.getAll()).hasSize(2);
        assertThat(filmStorage.delete(22)).isFalse();
        assertThat(filmStorage.getAll()).hasSize(2);
    }

    @Test
    void getAll() {
        List<Film> films = filmStorage.getAll();
        assertThat(films).hasSize(3);
        assertThat(films.get(0).getId()).isEqualTo(1);
        assertThat(films.get(0).getName()).isEqualTo("Film");
    }

    @Test
    void getById() {
        Film film = filmStorage.getById(1);
        assertThat(film.getName()).isEqualTo("Film");
        film = filmStorage.getById(9);
        assertThat(film).isNull();
    }

    @Test
    void getByIdSet() {
        List<Long> idsFilms = List.of(3L, 1L, 2L);
        List<Film> films = filmStorage.getByIdSet(idsFilms, false);
        assertThat(films).hasSize(3);
        Film film = films.get(0);
        assertThat(film.getId()).isEqualTo(1);
        assertThat(film.getName()).isEqualTo("Film");
    }

    @Test
    void GetSortedListByIdSet() {
        List<Long> idsFilms = List.of(3L, 1L, 2L);
        List<Film> films = filmStorage.getByIdSet(idsFilms, true);
        assertThat(films).hasSize(3);
        Film film = films.get(1);
        assertThat(film.getId()).isEqualTo(1);
        assertThat(film.getName()).isEqualTo("Film");
    }

    @Test
    void addAndGetLike() {
        filmStorage.addLike(1, 1);
        Film film = filmStorage.getById(1);
        assertThat(film.getLikes()).hasSize(1);
        assertThat(film.getLikes().get(0)).isEqualTo(1);
    }

    @Test
    void delLike() {
        filmStorage.addLike(1, 1);
        filmStorage.delLike(1, 1);
        Film film = filmStorage.getById(1);
        assertThat(film.getLikes()).hasSize(0);
    }

    @Test
    void getPopular() {
        filmStorage.addLike(3, 1);
        List<Long> filmsIds = filmStorage.getPopular(10);
        assertThat(filmsIds).hasSize(3);
        assertThat(filmsIds.get(0)).isEqualTo(3);
        assertThat(filmsIds.get(1)).isEqualTo(1);
        assertThat(filmsIds.get(2)).isEqualTo(2);

        filmStorage.addLike(3, 2);
        filmStorage.addLike(2, 2);
        filmsIds = filmStorage.getPopular(10);
        assertThat(filmsIds).hasSize(3);
        assertThat(filmsIds.get(0)).isEqualTo(3);
        assertThat(filmsIds.get(1)).isEqualTo(2);
        assertThat(filmsIds.get(2)).isEqualTo(1);

        filmStorage.addLike(3, 2);
        filmStorage.addLike(2, 2);
        filmsIds = filmStorage.getPopular(10);
        assertThat(filmsIds).hasSize(3);
        assertThat(filmsIds.get(0)).isEqualTo(3);
        assertThat(filmsIds.get(1)).isEqualTo(2);
        assertThat(filmsIds.get(2)).isEqualTo(1);
    }

    private static Film getFilm(){
        Film film = Film.builder()
                .name("tolko")
                .description("bla bla bla")
                .releaseDate(LocalDate.EPOCH)
                .duration(178)
                .mpa(MPA.builder().id(1).build())
                .build();
        film.getGenres().add(Genre.builder().id(3).build());
        film.getGenres().add(Genre.builder().id(1).build());
        return film;
    }
}