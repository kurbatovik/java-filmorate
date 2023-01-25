package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface Storage<E> {

    E create(E entity);

    E update(long id, E entity);

    boolean delete(long id);

    List<E> getAll();

    Optional<E> getById(long id);

    List<E> getByIdSet(Collection<Long> ids);
}
