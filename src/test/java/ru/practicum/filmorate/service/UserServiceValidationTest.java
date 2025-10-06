package ru.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.filmorate.exception.NotFoundException;
import ru.practicum.filmorate.model.User;
import ru.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceValidationTest {

    private UserService service;

    @BeforeEach
    void setUp() {
        var storage = new InMemoryUserStorage();
        service = new UserService(storage);
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
