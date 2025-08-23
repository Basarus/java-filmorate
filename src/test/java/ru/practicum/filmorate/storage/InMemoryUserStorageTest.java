package ru.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryUserStorageTest {

    private InMemoryUserStorage storage;

    @BeforeEach
    void setUp() {
        storage = new InMemoryUserStorage();
    }

    private User make() {
        User u = new User();
        u.setEmail("a@b.com");
        u.setLogin("x");
        u.setName("X");
        u.setBirthday(LocalDate.of(2000,1,1));
        return u;
    }

    @Test
    void saveAssignsIdAndStores() {
        User saved = storage.save(make());
        assertNotNull(saved.getId());
        assertTrue(storage.exists(saved.getId()));
        assertEquals(1, storage.findAll().size());
    }

    @Test
    void updateOverwritesEntry() {
        User saved = storage.save(make());
        saved.setName("Y");
        storage.update(saved);
        assertEquals("Y", storage.findById(saved.getId()).orElseThrow().getName());
    }

    @Test
    void findByIdOptional() {
        assertTrue(storage.findById(777).isEmpty());
    }
}
