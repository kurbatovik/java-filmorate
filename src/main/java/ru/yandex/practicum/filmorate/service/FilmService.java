package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
public class FilmService extends AbstractService<Film> {

    private final UserService userService;
    private final Map<Long, Integer> likedFilms;

    @Autowired
    public FilmService(InMemoryStorage<Film> storage, UserService userService) {
        super(storage);
        this.userService = userService;
        likedFilms = new HashMap<>();
    }

    @Override
    public Film create(Film film) {
        return storage.create(film);
    }

    @Override
    public Film update(long id, Film film) {
        Film updateFilm = storage.getById(film.getId());
        updateFilm.setName(film.getName());
        updateFilm.setDescription(film.getDescription());
        updateFilm.setReleaseDate(film.getReleaseDate());
        updateFilm.setDuration(film.getDuration());
        return storage.update(id, updateFilm);
    }

    public List<User> addLike(long id, long userId) {
        Film film = findById(id);
        User user = userService.findById(userId);
        likedFilms.put(id, likedFilms.getOrDefault(id, 0) + 1);
        film.getLikes().add(userId);
        user.getLikedFilms().add(id);
        return userService.getList(film.getLikes());
    }

    public List<User> delLike(long id, long userId) {
        Film film = findById(id);
        User user = userService.findById(userId);
        likedFilms.put(id, likedFilms.getOrDefault(id, 1) - 1);
        film.getLikes().remove(userId);
        user.getLikedFilms().remove(id);
        return userService.getList(film.getLikes());
    }

    public List<Film> findPopularFilms(int count) {
        Comparator<Film> byLikes = Comparator.comparingInt(o -> o.getLikes().size());
        return storage.getAll().stream().sorted(byLikes.reversed())
                .limit(count).collect(Collectors.toList());
    }
}
