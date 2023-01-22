package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Model;
import ru.yandex.practicum.filmorate.service.AbstractService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
public abstract class AbstractController<E extends Model> {

    protected AbstractService<E> service;

    @GetMapping("")
    public List<E> get() {
        log.info("Request on get all");
        return service.findAll();
    }

    @GetMapping("/{id}")
    public E get(@PathVariable(name = "id") Integer id) {
        log.info("Request on get with id {}", id);
        return service.findById(id);
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") Integer id) {
        log.info("Request on delete with id {}", id);
        return service.deleteById(id) ? "Success" : "Failure";
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(value = "")
    public E post(@Valid @RequestBody @NonNull E entity) {
        log.debug("Request on create: {}", entity);
        E newEntity = service.create(entity);
        log.info("Has been created: {}", newEntity);
        return newEntity;
    }

    @PutMapping(value = "")
    public E put(@Valid @RequestBody @NonNull E entity) {
        return put(entity, entity.getId());
    }

    @PutMapping(value = "/{id}")
    public E put(@Valid @RequestBody @NonNull E entity, @PathVariable(name = "id") long id) {
        log.debug("Request on update: {}", entity);
        E updateEntity = service.update(id, entity);
        log.info("Updated: {}", updateEntity);
        return updateEntity;
    }

}
