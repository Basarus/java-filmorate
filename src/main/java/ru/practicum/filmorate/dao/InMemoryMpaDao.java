package ru.practicum.filmorate.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Primary
@Profile("test")
public class InMemoryMpaDao extends MpaDao {

    public InMemoryMpaDao() {
        super(null);
    }

    @Override
    public boolean exists(int id) {
        return id >= 1 && id <= 5;
    }

    @Override
    public java.util.List<ru.practicum.filmorate.model.Mpa> findAll() {
        return java.util.List.of(new ru.practicum.filmorate.model.Mpa(1, "G"), new ru.practicum.filmorate.model.Mpa(2, "PG"), new ru.practicum.filmorate.model.Mpa(3, "PG-13"), new ru.practicum.filmorate.model.Mpa(4, "R"), new ru.practicum.filmorate.model.Mpa(5, "NC-17"));
    }

    @Override
    public java.util.Optional<ru.practicum.filmorate.model.Mpa> findById(int id) {
        return findAll().stream().filter(mpa -> mpa.getId() == id).findFirst();
    }
}
