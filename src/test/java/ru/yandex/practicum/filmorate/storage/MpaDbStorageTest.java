package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/schema.sql", "/test-data.sql"})
class MpaDbStorageTest {

    private final MpaDbStorage mpaStorage;

    @Test
    void create() {
        MPA mpa = getMPA();
        assertThat(mpaStorage.getAll()).hasSize(5);
        mpa = mpaStorage.create(mpa);
        assertThat(mpaStorage.getAll()).hasSize(6);
        assertThat(mpa.getId()).isEqualTo(6);
    }

    @Test
    void update() {
        MPA mpa = mpaStorage.getById(1).get();
        mpa.setName("AG-77");
        mpaStorage.update(mpa.getId(), mpa);
        assertThat(mpaStorage.getById(1).get().getName()).isEqualTo("AG-77");
    }

    @Test
    void delete() {
        assertThat(mpaStorage.getAll()).hasSize(5);
        assertThat(mpaStorage.delete(5)).isTrue();
        assertThat(mpaStorage.getAll()).hasSize(4);
        assertThat(mpaStorage.delete(22)).isFalse();
        assertThat(mpaStorage.getAll()).hasSize(4);
    }

    @Test
    void getAll() {
        List<MPA> mpas = mpaStorage.getAll();
        assertThat(mpas).hasSize(5);
        assertThat(mpas.get(0).getId()).isEqualTo(1);
        assertThat(mpas.get(0).getName()).isEqualTo("G");
    }

    @Test
    void getById() {
        MPA mpa = mpaStorage.getById(1).orElse(null);
        assertThat(mpa.getName()).isEqualTo("G");
        mpa = mpaStorage.getById(9).orElse(null);
        assertThat(mpa).isNull();
    }

    @Test
    void getByIdSet() {
        List<Long> idsMPAs = List.of(3L, 1L, 2L);
        List<MPA> mpas = mpaStorage.getByIdSet(idsMPAs);
        assertThat(mpas).hasSize(3);
        MPA mpa = mpas.get(0);
        assertThat(mpa.getId()).isEqualTo(1);
        assertThat(mpa.getName()).isEqualTo("G");
    }
    

    private static MPA getMPA(){
        return MPA.builder()
                .name("GG-21")
                .build();
    }
}