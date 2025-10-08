package ru.practicum.filmorate.dao;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("test")
public class InMemoryGenreDao extends GenreDao {
    private final Map<Integer, Genre> genres = new ConcurrentHashMap<>();

    public InMemoryGenreDao() {
        super(null);
        genres.put(1, new Genre(1, "Комедия"));
        genres.put(2, new Genre(2, "Драма"));
        genres.put(3, new Genre(3, "Мультфильм"));
        genres.put(4, new Genre(4, "Триллер"));
        genres.put(5, new Genre(5, "Документальный"));
        genres.put(6, new Genre(6, "Боевик"));
    }

    @Override
    public List<Genre> findAll() {
        return genres.values().stream().sorted(Comparator.comparingInt(Genre::getId)).toList();
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
        if (ids == null || ids.isEmpty()) return true;
        return ids.stream().allMatch(genres::containsKey);
    }
}
