package ru.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryFilmStorageTest {

    private InMemoryFilmStorage storage;

    @BeforeEach
    void setUp() {
        storage = new InMemoryFilmStorage();
    }

    private Film make() {
        Film f = new Film();
        f.setName("N");
        f.setDescription("D");
        f.setReleaseDate(LocalDate.of(2000,1,1));
        f.setDuration(90);
        return f;
    }

    @Test
    void saveAssignsIdAndStores() {
        Film saved = storage.save(make());
        assertNotNull(saved.getId());
        assertTrue(storage.exists(saved.getId()));
        assertEquals(1, storage.findAll().size());
    }

    @Test
    void updateOverwritesEntry() {
        Film saved = storage.save(make());
        saved.setName("New");
        storage.update(saved);
        assertEquals("New", storage.findById(saved.getId()).orElseThrow().getName());
    }

    @Test
    void findByIdOptional() {
        assertTrue(storage.findById(999).isEmpty());
    }
}
