package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RequestMapping("/films")
@RestController
public class FilmsController extends AbstractController {


    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping("")
    public List<Film> getFilms() {
        log.info("Film count: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(value = "")
    public Film postFilm(@Validated @RequestBody @NonNull Film film) {
        log.debug("Request on create Film: {}", film);
        int filmId = counter.incrementAndGet();
        Film newFilm = Film.builder()
                .id(filmId)
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .build();
        films.put(filmId, newFilm);
        log.info("Film has been created: {}", newFilm);
        log.debug("Film count: {}", films.size());
        return newFilm;
    }

    @PutMapping(value = "")
    public Film putFilm(@Validated @RequestBody @NonNull Film film) {
        return putFilm(film, film.getId());
    }

    @PutMapping(value = "/{id}")
    public Film putFilm(@Validated @RequestBody @NonNull Film film, @PathVariable(name = "id") int filmID) {
        log.debug("Request on update Film: {}", film);
        Film updateFilm = films.get(film.getId());
        if (updateFilm == null) {
            String error = String.format("Film with id: %d not found", filmID);
            log.info(error);
            throw new ResponseStatusException(NOT_FOUND,
                    error);
        }
        log.debug("Film has been found to update: {}", updateFilm);
        updateFilm.setName(film.getName());
        updateFilm.setDescription(film.getDescription());
        updateFilm.setReleaseDate(film.getReleaseDate());
        updateFilm.setDuration(film.getDuration());
        log.info("Film updated: {}", updateFilm);
        return updateFilm;
    }

}
