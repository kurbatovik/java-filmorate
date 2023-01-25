package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Model;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class AbstractService<E extends Model> {

    protected Storage<E> storage;

    public AbstractService(Storage<E> storage) {
        this.storage = storage;
    }

    public List<E> findAll() {
        return new ArrayList<>(storage.getAll());
    }

    public E findById(long id) {
        Optional<E> entity = storage.getById(id);
        return entity.orElseThrow(() -> new NotFoundException(String.format("ID: %d not found", id)));
    }

    public List<E> getList(Collection<Long> ids) {
        return storage.getByIdSet(ids);
    }

    public abstract E create(E e);

    public abstract E update(long id, E e);

    public boolean deleteById(long id) {
        return storage.delete(id);
    }
}
