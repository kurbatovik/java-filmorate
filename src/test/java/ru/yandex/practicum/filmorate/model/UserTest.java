package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import org.springframework.validation.annotation.Validated;

import javax.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Validated
class UserTest extends Validatable {

    @Test
    void idFieldTest() {
        User user = new User(-1, "test@yandex.ru", "login",
                "", LocalDate.of(2001, 10, 30));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("ID cannot be negative", violations.iterator().next().getMessage());

        user = new User(0, "test@yandex.ru", "login",
                "", LocalDate.of(2001, 10, 30));
        violations = validator.validate(user);
        assertEquals(0, violations.size());

        user = new User(Integer.MAX_VALUE, "test@yandex.ru", "login",
                "", LocalDate.of(2001, 10, 30));
        violations = validator.validate(user);
        assertEquals(0, violations.size());
    }

    @Test
    void emailFieldTest() {
        User user = new User(1, "test@yandex", "login",
                "", LocalDate.of(2001, 10, 30));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Email is wrong", violations.iterator().next().getMessage());

        user = new User(1, "test&^%@yandex.ru", "login",
                "", LocalDate.of(2001, 10, 30));
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Email is wrong", violations.iterator().next().getMessage());

        user = new User(1, "", "login",
                "", LocalDate.of(2001, 10, 30));
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Email is wrong", violations.iterator().next().getMessage());

        user = new User(1, null, "login",
                "", LocalDate.of(2001, 10, 30));
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Email can not be null", violations.iterator().next().getMessage());
    }

    @Test
    void loginFieldTest() {
        User user = new User(1, "yandex-user@cloud.yandex.ru", "",
                "", LocalDate.of(2001, 10, 30));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Login can not be blank and contains spaces", violations.iterator().next().getMessage());

        user = new User(1, "yandex-user@cloud.yandex.ru", "Test User",
                "", LocalDate.of(2001, 10, 30));
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Login can not be blank and contains spaces", violations.iterator().next().getMessage());

        user = new User(1, "yandex-user@cloud.yandex.ru", "  ",
                "", LocalDate.of(2001, 10, 30));
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Login can not be blank and contains spaces", violations.iterator().next().getMessage());


        user = new User(1, "yandex-user@cloud.yandex.ru", null,
                "", LocalDate.of(2001, 10, 30));
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Login can not be null", violations.iterator().next().getMessage());
    }

    @Test
    void birthdayFieldTest() {
        User user = new User(1, "yandex-user@cloud.yandex.ru", "Vasya",
                "", LocalDate.of(2031, 10, 30));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Birthdate can not be in future", violations.iterator().next().getMessage());


        user = new User(1, "yandex-user@cloud.yandex.ru", "Vasya",
                "", null);
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Birthdate can not be null", violations.iterator().next().getMessage());
    }
}