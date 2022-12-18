package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import org.springframework.validation.annotation.Validated;

import javax.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Validated
class FilmTest extends Validatable {

    @Test
    void idFieldTest() {
        Film film = new Film(-1, "film film film", "The best movie ever",
                LocalDate.of(2001, 10, 30), 200);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("ID cannot be negative", violations.iterator().next().getMessage());

        film = new Film(0, "film film film", "The best movie ever",
                LocalDate.of(2001, 10, 30), 200);
        violations = validator.validate(film);
        assertEquals(0, violations.size());

        film = new Film(Integer.MAX_VALUE, "Film film film", "The best movie ever",
                LocalDate.of(2001, 10, 30), 200);
        violations = validator.validate(film);
        assertEquals(0, violations.size());
    }

    @Test
    void nameFieldTest() {
        Film film = new Film(0, "  ", "The best movie ever",
                LocalDate.of(2001, 10, 30), 200);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Name can not be blank", violations.iterator().next().getMessage());

        film = new Film(1, "f ", "The best movie ever",
                LocalDate.of(2001, 10, 30), 200);
        violations = validator.validate(film);
        assertEquals(0, violations.size());

        film = new Film(2, " f ", "The best movie ever",
                LocalDate.of(2001, 10, 30), 200);
        violations = validator.validate(film);
        assertEquals(0, violations.size());

        film = new Film(3, null, "The best movie ever",
                LocalDate.of(2001, 10, 30), 200);
        violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Name can not be blank", violations.iterator().next().getMessage());
    }

    @Test
    void descriptionFieldTest() {
        Film film = new Film(0, "Film film", "",
                LocalDate.of(2001, 10, 30), 200);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Description can not be blank", violations.iterator().next().getMessage());

        film = new Film(1, "Film film", null,
                LocalDate.of(2001, 10, 30), 200);
        violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Description can not be blank", violations.iterator().next().getMessage());

        film = new Film(2, "Film film", "The best movie ever. The best movie ever. " +
                "The best movie ever. The best movie ever. The best movie ever. The best movie ever. " +
                "The best movie ever. The best movie ever. The best movie ever. The best movie ever.",
                LocalDate.of(2001, 10, 30), 200);
        violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Description can not be more 200 char", violations.iterator().next().getMessage());

        film = new Film(2, "Film film", "The best movie ever. The best movie ever. " +
                "The best movie ever. The best movie ever. The best movie ever. The best movie ever. " +
                "The best movie ever. The best movie ever. The best movie ever. The best mo",
                LocalDate.of(2001, 10, 30), 200);
        violations = validator.validate(film);
        assertEquals(0, violations.size());
    }

    @Test
    void releaseDateFieldTest() {
        Film film = new Film(0, "Film film", "The best movie ever.",
                LocalDate.of(1895, 12, 27), 200);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Release date cannot be earlier than December 28, 1895",
                violations.iterator().next().getMessage());

        film = new Film(1, "Film film", "The best movie ever.",
                LocalDate.of(1895, 12, 28), 200);
        violations = validator.validate(film);
        assertEquals(0, violations.size());

        film = new Film(2, "Film film", "The best movie ever.",
                LocalDate.of(2095, 12, 27), 200);
        violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Release date can not be in future", violations.iterator().next().getMessage());

        film = new Film(1, "Film film", "The best movie ever.",
                LocalDate.now(), 200);
        violations = validator.validate(film);
        assertEquals(0, violations.size());
    }

    @Test
    void durationFieldTest() {
        Film film = new Film(1, "film film film", "The best movie ever",
                LocalDate.of(2001, 10, 30), -1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Duration can not be negative or zero", violations.iterator().next().getMessage());

        film = new Film(2, "film film film", "The best movie ever",
                LocalDate.of(2001, 10, 30), 0);
        violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Duration can not be negative or zero", violations.iterator().next().getMessage());

        film = new Film(3, "Film film film", "The best movie ever",
                LocalDate.of(2001, 10, 30), 2);
        violations = validator.validate(film);
        assertEquals(0, violations.size());

        film = new Film(4, "Film film film", "The best movie ever",
                LocalDate.of(2001, 10, 30), Integer.MAX_VALUE);
        violations = validator.validate(film);
        assertEquals(0, violations.size());
    }
}