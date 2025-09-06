package ru.practicum.filmorate.service;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.filmorate.exception.ValidationException;
import ru.practicum.filmorate.model.Film;
import ru.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmServiceValidationTest {

    private FilmService service;

    @BeforeEach
    void setUp() {
        var filmStorage = new InMemoryFilmStorage();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        var userStorage = new InMemoryUserStorage();
        service = new FilmService(filmStorage, validator, userStorage);
    }

    private Film validFilm() {
        Film f = new Film();
        f.setName("Name");
        f.setDescription("Desc");
        f.setReleaseDate(LocalDate.of(2000, 1, 1));
        f.setDuration(100);
        return f;
    }

    @Test
    void rejectBlankName() {
        Film f = validFilm();
        f.setName("   ");
        assertThrows(ValidationException.class, () -> service.create(f));
    }

    @Test
    void rejectTooLongDescription() {
        Film f = validFilm();
        f.setDescription("x".repeat(201));
        assertThrows(ValidationException.class, () -> service.create(f));
    }

    @Test
    void rejectReleaseBeforeCinemaBirthday() {
        Film f = validFilm();
        f.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ValidationException.class, () -> service.create(f));
    }

    @Test
    void rejectNonPositiveDuration() {
        Film f = validFilm();
        f.setDuration(0);
        assertThrows(ValidationException.class, () -> service.create(f));
    }

    @Test
    void acceptValidFilm() {
        Film f = validFilm();
        assertDoesNotThrow(() -> service.create(f));
    }
}
