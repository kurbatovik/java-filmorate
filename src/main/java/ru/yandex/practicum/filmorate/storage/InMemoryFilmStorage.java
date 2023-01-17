package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

@Slf4j
@Component
public class InMemoryFilmStorage extends InMemoryStorage<Film> {

    @Override
    public Film create(Film film) {
        Film newFilm = Film.builder()
                .id(getNextId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .build();
        put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public boolean delete(long id) {
        return remove(id);
    }
}
