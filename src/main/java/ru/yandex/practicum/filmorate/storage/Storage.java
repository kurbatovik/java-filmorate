package ru.yandex.practicum.filmorate.storage;

import java.util.List;


public interface Storage<E> {

    E create(E entity);

    E update(long id, E entity);

    boolean delete(long id);

    List<E> getAll();

    E getById(long id);
}
