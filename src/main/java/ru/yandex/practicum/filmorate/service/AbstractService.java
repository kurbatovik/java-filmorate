package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Model;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
public abstract class AbstractService<E extends Model> {

    protected final Storage<E> storage;

    public AbstractService(Storage<E> storage) {
        this.storage = storage;
    }

    public List<E> findAll() {
        return new ArrayList<>(storage.getAll());
    }

    public E findById(long id) {
        E entity = storage.getById(id);
        if (entity == null) {
            String error = String.format("ID: %d not found", id);
            log.info(error);
            throw new ResponseStatusException(NOT_FOUND,
                    error);
        }
        return entity;
    }

    public List<E> getList(Collection<Long> ids) {
        return storage.getByIdSet(ids);
    }

    public List<E> getList(Collection<Long> ids, boolean isSort) {
        return storage.getByIdSet(ids, isSort);
    }

    public abstract E create(E e);

    public abstract E update(long id, E e);

    public boolean deleteById(long id) {
        return storage.delete(id);
    }
}
