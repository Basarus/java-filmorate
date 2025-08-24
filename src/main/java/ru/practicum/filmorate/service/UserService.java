package ru.practicum.filmorate.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.filmorate.exception.NotFoundException;
import ru.practicum.filmorate.exception.ValidationException;
import ru.practicum.filmorate.model.User;
import ru.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final UserStorage storage;
    private final Validator validator;

    public UserService(UserStorage storage, Validator validator) {
        this.storage = storage;
        this.validator = validator;
    }

    public UserService(UserStorage storage) {
        this.storage = storage;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public User create(User user) {
        validateBean(user);
        normalize(user);
        User saved = storage.save(user);
        log.info("User created: {} {}", saved.getId(), saved.getLogin());
        return saved;
    }

    public User update(User user) {
        if (user.getId() == null || !storage.exists(user.getId())) {
            throw new NotFoundException("User id=" + user.getId() + " not found");
        }
        validateBean(user);
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

    private void validateBean(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            String msg = violations.iterator().next().getMessage();
            throw new ValidationException(msg);
        }
    }
}
