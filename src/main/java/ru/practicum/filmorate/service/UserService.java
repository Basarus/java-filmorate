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
        validate(user);
        normalize(user);
        User saved = storage.save(user);
        log.info("User created: {} {}", saved.getId(), saved.getLogin());
        return saved;
    }

    public User update(User user) {
        if (user.getId() == null || !storage.exists(user.getId())) {
            throw new NotFoundException("User id=" + user.getId() + " not found");
        }
        validate(user);
        normalize(user);
        User updated = storage.update(user);
        log.info("User updated: {} {}", updated.getId(), updated.getLogin());
        return updated;
    }

    public List<User> findAll() {
        return storage.findAll();
    }

    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("email must contain @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("login must not be blank and must not contain spaces");
        }
        if (user.getBirthday() == null) {
            throw new ValidationException("birthday must not be null");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("birthday must not be in the future");
        }
    }

    private void normalize(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
