package ru.practicum.filmorate.storage.film;

import ru.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film save(Film film);

    Film update(Film film);

    List<Film> findAll();

    Optional<Film> findById(int id);

    boolean exists(int id);
}
