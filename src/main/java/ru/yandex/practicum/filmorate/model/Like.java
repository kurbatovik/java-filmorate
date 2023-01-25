package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Like implements Model {

    private long id;
    private long filmId;
    private long userId;
}
