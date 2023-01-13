package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Model;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractService<E extends Model> {

    protected final Storage<E> storage;

    public AbstractService(InMemoryStorage<E> storage) {
        this.storage = storage;
    }

    public List<E> findAll() {
        return new ArrayList<>(storage.getAll());
    }

    public E findById(long id) {
        return storage.getById(id);
    }

    public List<E> getList(Set<Long> ids) {
        return ids.stream().map(this::findById).collect(Collectors.toList());
    }

    public abstract E create(E e);

    public abstract E update(long id, E e);

}
