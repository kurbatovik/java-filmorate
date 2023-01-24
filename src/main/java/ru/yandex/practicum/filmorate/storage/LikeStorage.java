package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;

public interface LikeStorage extends Storage<Like>{

    void addLike(long filmId, long userId);

    List<Long> getLikesByFilmId(long id);

    void delLike(long id, long userId);

    List<Film> getPopular(int count);
}
