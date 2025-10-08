package ru.practicum.filmorate.storage.film;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.practicum.filmorate.model.Film;

import java.util.*;

@Component
@Profile("test")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @Override
    public Film save(Film film) {
        film.setId(id++);
        Film test = films.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> findById(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public boolean exists(int id) {
        return films.containsKey(id);
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }
}

