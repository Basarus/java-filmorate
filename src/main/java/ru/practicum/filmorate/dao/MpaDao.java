package ru.practicum.filmorate.dao;

import ru.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaDao {
    List<Mpa> findAll();

    Optional<Mpa> findById(int id);

    boolean exists(int id);
}
