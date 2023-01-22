package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.List;


public interface Storage<E> {

    E create(E entity);

    E update(long id, E entity);

    boolean delete(long id);

    List<E> getAll();

    E getById(long id);

    List<E> getByIdSet(Collection<Long> ids);

    List<E> getByIdSet(Collection<Long> ids, boolean isSort);
}
