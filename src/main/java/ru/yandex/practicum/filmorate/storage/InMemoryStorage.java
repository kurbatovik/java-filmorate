package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Model;

import java.util.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
public abstract class InMemoryStorage<E extends Model> implements Storage<E> {

    private final Map<Long, E> entities = new HashMap<>();

    private int counter;

    public List<E> getAll() {
        return new ArrayList<>(values());
    }

    public E getById(long id) {
        return get(id);
    }

    public E update(long id, E e) {
        return put(id, e);
    }

    protected E put(long id, E entitie) {
        return entities.put(id, entitie);
    }

    protected E get(long id) {
        E entitie = entities.get(id);
        if (entitie == null) {
            String error = String.format("ID: %d not found", id);
            log.info(error);
            throw new ResponseStatusException(NOT_FOUND,
                    error);
        }
        return entitie;
    }

    protected Collection<E> values() {
        return entities.values();
    }

    protected boolean remove(long id) {
        return entities.remove(id, get(id));
    }

    protected int getNextId() {
        return ++counter;
    }

}
