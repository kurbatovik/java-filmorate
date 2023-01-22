package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.AbstractService;


@Slf4j
@RequestMapping("/genres")
@RestController
public class GenreController extends AbstractController<Genre> {

    @Autowired
    public GenreController(AbstractService<Genre> service) {
        this.service = service;
    }
}
