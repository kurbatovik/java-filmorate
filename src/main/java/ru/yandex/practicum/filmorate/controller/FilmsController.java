package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.AbstractService;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;


@Slf4j
@RequestMapping("/films")
@RestController
public class FilmsController extends AbstractController<Film> {

    @Autowired
    public FilmsController(AbstractService<Film> service) {
        this.service = service;
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public List<User> addFriend(@PathVariable(value = "id") long id, @PathVariable(value = "userId") long userId) {
        return getFilmService().addLike(id, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public List<User> delFriend(@PathVariable(value = "id") long id, @PathVariable(value = "userId") long userID) {
        return getFilmService().delLike(id, userID);
    }

    @GetMapping(value = "/popular")
    public List<Film> popularFilm(@RequestParam(value = "count", required = false, defaultValue = "10") String count) {
        int filmsCount = Integer.parseInt(count);
        return getFilmService().findPopularFilms(filmsCount);
    }

    private FilmService getFilmService() {
        return (FilmService) service;
    }

}
