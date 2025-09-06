package ru.practicum.filmorate.storage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseInMemoryStorage<T extends Identifiable> {
    protected final Map<Integer, T> storage = new LinkedHashMap<>();
    private final AtomicInteger idSeq = new AtomicInteger(0);

    public T save(T entity) {
        int id = idSeq.incrementAndGet();
        entity.setId(id);
        storage.put(id, entity);
        return entity;
    }

    public T update(T entity) {
        storage.put(entity.getId(), entity);
        return entity;
    }

    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<T> findById(int id) {
        return Optional.ofNullable(storage.get(id));
    }

    public boolean exists(int id) {
        return storage.containsKey(id);
    }
}
