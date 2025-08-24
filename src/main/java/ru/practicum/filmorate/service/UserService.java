package ru.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.filmorate.exception.NotFoundException;
import ru.practicum.filmorate.exception.ValidationException;
import ru.practicum.filmorate.model.User;
import ru.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage storage;

    public User create(User user) {
        normalize(user);
        User saved = storage.save(user);
        log.info("User created: {} {}", saved.getId(), saved.getLogin());
        return saved;
    }

    public User update(User user) {
        if (user.getId() == null || !storage.exists(user.getId())) {
            throw new NotFoundException("User id=" + user.getId() + " not found");
        }
        normalize(user);
        User updated = storage.update(user);
        log.info("User updated: {} {}", updated.getId(), updated.getLogin());
        return updated;
    }

    public List<User> findAll() {
        return storage.findAll();
    }

    private void normalize(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
