package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.AbstractService;


@Slf4j
@RequestMapping("/mpa")
@RestController
public class MPAController extends AbstractController<MPA> {

    @Autowired
    public MPAController(AbstractService<MPA> service) {
        this.service = service;
    }
}
