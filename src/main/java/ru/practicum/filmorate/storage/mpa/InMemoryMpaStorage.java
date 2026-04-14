package ru.practicum.filmorate.storage.mpa;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.practicum.filmorate.model.Mpa;

import java.util.*;

@Component
@Profile("test")
public class InMemoryMpaStorage implements MpaStorage {
    private final Map<Integer, Mpa> mpaRatings = new HashMap<>();

    public InMemoryMpaStorage() {
        mpaRatings.put(1, new Mpa(1, "G",     "Нет возрастных ограничений"));
        mpaRatings.put(2, new Mpa(2, "PG",    "Рекомендуется присутствие родителей"));
        mpaRatings.put(3, new Mpa(3, "PG-13", "Детям до 13 лет просмотр не рекомендуется"));
        mpaRatings.put(4, new Mpa(4, "R",     "Лицам до 17 лет обязательно присутствие взрослого"));
        mpaRatings.put(5, new Mpa(5, "NC-17", "Лицам до 17 лет просмотр запрещён"));
    }

    @Override
    public List<Mpa> findAll() {
        return new ArrayList<>(mpaRatings.values());
    }

    @Override
    public Optional<Mpa> findById(int id) {
        return Optional.ofNullable(mpaRatings.get(id));
    }

    @Override
    public boolean exists(int id) {
        return mpaRatings.containsKey(id);
    }
}
