package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;


@Service
@Slf4j
public class GenreService extends AbstractService<Genre> {

    @Autowired
    public GenreService(GenreDbStorage storage) {
        super(storage);
    }

    @Override
    public Genre create(Genre genre) {
        return storage.create(genre);
    }

    @Override
    public Genre update(long id, Genre genre) {
        Genre updateGenre = findById(genre.getId());
        updateGenre.setName(genre.getName());
        return storage.update(id, updateGenre);
    }

}
