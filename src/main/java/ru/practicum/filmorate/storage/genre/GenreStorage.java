package ru.practicum.filmorate.storage.genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {
    List<ru.practicum.filmorate.model.Genre> findAll();

    Optional<ru.practicum.filmorate.model.Genre> findById(int id);

    boolean exists(int id);

    boolean allExist(Set<Integer> ids);
}