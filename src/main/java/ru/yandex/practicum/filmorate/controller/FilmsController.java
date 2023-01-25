package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.AbstractService;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.constraints.Positive;
import java.util.List;


@Slf4j
@Validated
@RequestMapping("/films")
@RestController
public class FilmsController extends AbstractController<Film> {

    @Autowired
    public FilmsController(AbstractService<Film> service) {
        this.service = service;
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public List<User> addLike(@PathVariable(value = "id") @Positive long id,
                              @PathVariable(value = "userId") @Positive long userId) {
        return getFilmService().addLike(id, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public List<User> delLike(@PathVariable(value = "id") @Positive long id,
                              @PathVariable(value = "userId") @Positive long userID) {
        return getFilmService().delLike(id, userID);
    }

    @GetMapping(value = "/popular")
    public List<Film> popularFilm(@RequestParam(value = "count", defaultValue = "10") @Positive int count) {
        return getFilmService().findPopularFilms(count);
    }

    private FilmService getFilmService() {
        return (FilmService) service;
    }

}
