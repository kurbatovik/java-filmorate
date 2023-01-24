package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeStorage;


@Service
@Slf4j
public class LikeService extends AbstractService<Like> {

    @Autowired
    public LikeService(LikeStorage storage) {
        super(storage);
    }

    public Like create(Like like) {
        return storage.create(like);
    }

    public Like update(long id, Like like) {
        Like updateLike = findById(like.getId());
        updateLike.setUserId(like.getUserId());
        updateLike.setFilmId(like.getFilmId());
        return storage.update(id, updateLike);
    }

}
