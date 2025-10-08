package ru.practicum.filmorate.storage.mpa;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.practicum.filmorate.model.Mpa;

import java.util.*;

@Repository
@Profile("test")
public class InMemoryMpaStorage implements MpaStorage {

    private final Map<Integer, Mpa> data = Map.of(1, new Mpa(1, "G"), 2, new Mpa(2, "PG"), 3, new Mpa(3, "PG-13"), 4, new Mpa(4, "R"), 5, new Mpa(5, "NC-17"));

    @Override
    public List<Mpa> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Optional<Mpa> findById(int id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public boolean exists(int id) {
        return data.containsKey(id);
    }
}
