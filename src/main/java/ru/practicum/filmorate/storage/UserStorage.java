package ru.practicum.filmorate.storage;

import ru.practicum.filmorate.model.User;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User save(User user);
    User update(User user);
    List<User> findAll();
    Optional<User> findById(int id);
    boolean exists(int id);
}
