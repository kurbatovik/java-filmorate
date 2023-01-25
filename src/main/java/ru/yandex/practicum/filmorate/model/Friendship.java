package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public
class Friendship {
    private long id;
    private long userId;
    private long friendId;
    private boolean accepted;
}