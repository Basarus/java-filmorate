package ru.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.filmorate.model.Film;
import ru.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.practicum.filmorate.storage.genre.InMemoryGenreStorage;
import ru.practicum.filmorate.storage.likes.InMemoryLikesStorage;
import ru.practicum.filmorate.storage.mpa.InMemoryMpaStorage;
import ru.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class FilmServiceValidationTest {

    private FilmService service;

    @BeforeEach
    void setUp() {
        var filmStorage = new InMemoryFilmStorage();
        var userStorage = new InMemoryUserStorage();
        var filmQueryService = new InMemoryFilmQueryService(filmStorage, new InMemoryMpaStorage(), new InMemoryGenreStorage(), new InMemoryLikesStorage());
        service = new FilmService(filmStorage, userStorage, filmQueryService);
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
    void acceptValidFilm() {
        Film f = validFilm();
        assertDoesNotThrow(() -> service.create(f));
    }
}
