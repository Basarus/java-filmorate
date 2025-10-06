package ru.practicum.filmorate.dao;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import ru.practicum.filmorate.model.Genre;

public class InMemoryGenreDao implements GenreDao {
    private final Map<Integer, Genre> genres = new ConcurrentHashMap<>();

    public InMemoryGenreDao() {
        genres.put(1, new Genre(1, "Комедия"));
        genres.put(2, new Genre(2, "Драма"));
    }

    @Override
    public List<Genre> findAll() {
        return List.copyOf(genres.values());
    }

    @Override
    public Optional<Genre> findById(int id) {
        return Optional.ofNullable(genres.get(id));
    }

    @Override
    public boolean exists(int id) {
        return genres.containsKey(id);
    }

    @Override
    public boolean allExist(Set<Integer> ids) {
        return ids == null || ids.stream().allMatch(genres::containsKey);
    }
}
