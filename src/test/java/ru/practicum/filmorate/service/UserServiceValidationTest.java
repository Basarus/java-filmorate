package ru.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.filmorate.exception.NotFoundException;
import ru.practicum.filmorate.exception.ValidationException;
import ru.practicum.filmorate.model.User;
import ru.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceValidationTest {

    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService(new InMemoryUserStorage());
    }

    private User validUser() {
        User u = new User();
        u.setEmail("user@example.com");
        u.setLogin("neo");
        u.setName("");
        u.setBirthday(LocalDate.of(2000, 1, 1));
        return u;
    }

    @Test
    void rejectEmailWithoutAt() {
        User u = validUser();
        u.setEmail("not-email");
        assertThrows(ValidationException.class, () -> service.create(u));
    }

    @Test
    void rejectBlankEmail() {
        User u = validUser();
        u.setEmail("   ");
        assertThrows(ValidationException.class, () -> service.create(u));
    }

    @Test
    void rejectLoginWithSpaces() {
        User u = validUser();
        u.setLogin("bad login");
        assertThrows(ValidationException.class, () -> service.create(u));
    }

    @Test
    void rejectBlankLogin() {
        User u = validUser();
        u.setLogin("   ");
        assertThrows(ValidationException.class, () -> service.create(u));
    }

    @Test
    void rejectNullBirthday() {
        User u = validUser();
        u.setBirthday(null);
        assertThrows(ValidationException.class, () -> service.create(u));
    }

    @Test
    void rejectFutureBirthday() {
        User u = validUser();
        u.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> service.create(u));
    }

    @Test
    void nameFallbackToLoginWhenEmpty() {
        User u = validUser();
        User saved = service.create(u);
        assertEquals("neo", saved.getName());
    }

    @Test
    void acceptValidUser() {
        User u = validUser();
        User saved = service.create(u);
        assertNotNull(saved.getId());
    }

    @Test
    void updateUnknownIdThrows404() {
        User u = validUser();
        u.setId(777);
        assertThrows(NotFoundException.class, () -> service.update(u));
    }
}
