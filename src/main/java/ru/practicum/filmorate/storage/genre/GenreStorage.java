package ru.practicum.filmorate.storage.genre;

import ru.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {
    List<Genre> findAll();

    Optional<Genre> findById(int id);

    boolean allExist(Set<Integer> ids);

    boolean exists(int id);
}
