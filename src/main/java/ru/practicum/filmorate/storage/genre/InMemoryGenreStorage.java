package ru.practicum.filmorate.storage.genre;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.practicum.filmorate.model.Genre;

import java.util.*;

@Component
@Profile("test")
public class InMemoryGenreStorage implements GenreStorage {
    private final Map<Integer, Genre> genres = new HashMap<>();

    public InMemoryGenreStorage() {
        genres.put(1, new Genre(1, "Комедия"));
        genres.put(2, new Genre(2, "Драма"));
        genres.put(3, new Genre(3, "Мультфильм"));
        genres.put(4, new Genre(4, "Триллер"));
        genres.put(5, new Genre(5, "Документальный"));
        genres.put(6, new Genre(6, "Боевик"));
    }

    @Override
    public List<Genre> findAll() {
        return new ArrayList<>(genres.values());
    }

    @Override
    public Optional<Genre> findById(int id) {
        return Optional.ofNullable(genres.get(id));
    }

    @Override
    public boolean allExist(Set<Integer> ids) {
        return ids.stream().allMatch(genres::containsKey);
    }

    @Override
    public boolean exists(int id) {
        return genres.containsKey(id);
    }
}
