package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@Slf4j
public class FilmService extends AbstractService<Film> {

    private final UserService userService;

    @Autowired
    public FilmService(FilmDbStorage storage, UserService userService) {
        super(storage);
        this.userService = userService;
    }

    @Override
    public Film create(Film film) {
        return storage.create(film);
    }

    @Override
    public Film update(long id, Film film) {
        Film updateFilm = findById(film.getId());
        updateFilm.setName(film.getName());
        updateFilm.setDescription(film.getDescription());
        updateFilm.setReleaseDate(film.getReleaseDate());
        updateFilm.setDuration(film.getDuration());
        updateFilm.setMpa(film.getMpa());
        updateFilm.getGenres().clear();
        updateFilm.getGenres().addAll(film.getGenres());
        return storage.update(id, updateFilm);
    }

    public List<User> addLike(long id, long userId) {
        findById(id);
        userService.findById(userId);
        ((FilmDbStorage)storage).addLike(id, userId);
        return userService.getList(getLikes(id));
    }

    private Set<Long> getLikes(long id) {
        return new HashSet<>(((FilmDbStorage)storage).getLikes(id));
    }

    public List<User> delLike(long id, long userId) {
        findById(id);
        userService.findById(userId);
        ((FilmDbStorage)storage).delLike(id, userId);
        return userService.getList(getLikes(id));
    }

    public List<Film> findPopularFilms(int count) {
        return getList(((FilmDbStorage)storage).getPopular(count), true);
    }
}
