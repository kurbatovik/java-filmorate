package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;


@Service
@Slf4j
public class MPAService extends AbstractService<MPA> {

    @Autowired
    public MPAService(MpaDbStorage storage) {
        super(storage);
    }

    @Override
    public MPA create(MPA mpa) {
        return storage.create(mpa);
    }

    @Override
    public MPA update(long id, MPA mpa) {
        MPA updateMPA = storage.getById(mpa.getId());
        updateMPA.setName(mpa.getName());
        return storage.update(id, updateMPA);
    }

}
