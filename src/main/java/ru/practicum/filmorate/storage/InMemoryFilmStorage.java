package ru.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.practicum.filmorate.model.Film;

@Component
public class InMemoryFilmStorage extends BaseInMemoryStorage<Film> implements FilmStorage {
}