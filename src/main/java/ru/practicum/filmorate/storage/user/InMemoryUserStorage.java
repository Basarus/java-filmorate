package ru.practicum.filmorate.storage.user;

import org.springframework.context.annotation.Profile;
import ru.practicum.filmorate.model.User;

import java.util.*;

@Profile("test")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int seq = 0;

    @Override
    public User save(User u) {
        u.setId(++seq);
        users.put(u.getId(), u);
        return u;
    }

    @Override
    public User update(User u) {
        users.put(u.getId(), u);
        return u;
    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean exists(int id) {
        return users.containsKey(id);
    }
}
