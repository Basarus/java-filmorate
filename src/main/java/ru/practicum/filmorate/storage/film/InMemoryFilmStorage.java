package ru.practicum.filmorate.storage.film;

import org.springframework.context.annotation.Profile;
import ru.practicum.filmorate.model.Film;
import ru.practicum.filmorate.storage.BaseInMemoryStorage;

@Profile("test")
public class InMemoryFilmStorage extends BaseInMemoryStorage<Film> implements FilmStorage {
}