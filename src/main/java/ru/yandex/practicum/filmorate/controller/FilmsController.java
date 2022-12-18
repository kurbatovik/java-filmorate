package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;

@Slf4j
@RequestMapping("/films")
@RestController
public class FilmsController extends AbstractController<Film> {


    public FilmsController() {
        entityName = "Film";
    }

    @Override
    protected Film create(Film film) {
        return Film.builder()
                .id(counter.incrementAndGet())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .build();

    }

    @Override
    protected Film update(int id, Film film, Film updateFilm) {
        updateFilm.setName(film.getName());
        updateFilm.setDescription(film.getDescription());
        updateFilm.setReleaseDate(film.getReleaseDate());
        updateFilm.setDuration(film.getDuration());
        return updateFilm;
    }

}
