package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.ModelInterface;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RequestMapping("/entities")
@RestController
public abstract class AbstractController<E extends ModelInterface> {

    protected final AtomicInteger counter = new AtomicInteger();
    protected final Map<Integer, E> entities = new HashMap<>();

    protected String entityName = "Abstract";

    @GetMapping("")
    public List<E> get() {
        log.info("{} count: {}", entityName, entities.size());
        return new ArrayList<>(entities.values());
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(value = "")
    public E post(@Valid @RequestBody @NonNull E entity) {
        log.debug("{} count: {}", entityName, entities.size());
        log.debug("Request on create {}: {}", entityName, entity);
        E newEntity = create(entity);
        entities.put(newEntity.getId(), newEntity);
        log.info("{} has been created: {}", entityName, newEntity);
        log.debug("{} count: {}", entityName, entities.size());
        return newEntity;
    }

    @PutMapping(value = "")
    public E put(@Valid @RequestBody @NonNull E entity) {
        return put(entity, entity.getId());
    }

    @PutMapping(value = "/{id}")
    public E put(@Valid @RequestBody @NonNull E entity, @PathVariable(name = "id") int id) {
        log.debug("Request on update {}: {}", entityName, entity);

        E updateEntity = entities.get(entity.getId());
        if (updateEntity == null) {
            String error = String.format("%s with id: %d not found", entityName, id);
            log.info(error);
            throw new ResponseStatusException(NOT_FOUND,
                    error);
        }
        updateEntity = update(id, entity, updateEntity);
        log.debug("{} has been found to update: {}", entityName, updateEntity);
        log.info("{} updated: {}", entityName, updateEntity);
        return updateEntity;
    }

    protected abstract E create(E entity);

    protected abstract E update(int id, E entity, E updateEntity);
}
