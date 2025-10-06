package ru.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.practicum.filmorate.model.Film;
import ru.practicum.filmorate.storage.BaseInMemoryStorage;

@Component
public class InMemoryFilmStorage extends BaseInMemoryStorage<Film> implements FilmStorage {
}