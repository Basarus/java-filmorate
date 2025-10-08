package ru.practicum.filmorate.storage.genre;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("test")
public class InMemoryGenreStorage implements GenreStorage {
    private final Map<Integer, ru.practicum.filmorate.model.Genre> genres = new ConcurrentHashMap<>();

    public InMemoryGenreStorage() {
        genres.put(1, new ru.practicum.filmorate.model.Genre(1, "Комедия"));
        genres.put(2, new ru.practicum.filmorate.model.Genre(2, "Драма"));
        genres.put(3, new ru.practicum.filmorate.model.Genre(3, "Мультфильм"));
        genres.put(4, new ru.practicum.filmorate.model.Genre(4, "Триллер"));
        genres.put(5, new ru.practicum.filmorate.model.Genre(5, "Документальный"));
        genres.put(6, new ru.practicum.filmorate.model.Genre(6, "Боевик"));
    }

    @Override
    public List<ru.practicum.filmorate.model.Genre> findAll() {
        return genres.values().stream().sorted(Comparator.comparingInt(ru.practicum.filmorate.model.Genre::getId)).toList();
    }

    @Override
    public Optional<ru.practicum.filmorate.model.Genre> findById(int id) {
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
