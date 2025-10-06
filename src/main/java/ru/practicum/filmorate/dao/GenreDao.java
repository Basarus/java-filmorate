package ru.practicum.filmorate.dao;

import ru.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreDao {
    List<Genre> findAll();

    Optional<Genre> findById(int id);

    boolean exists(int id);

    boolean allExist(Set<Integer> ids);
}