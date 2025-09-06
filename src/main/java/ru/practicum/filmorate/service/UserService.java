package ru.practicum.filmorate.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.filmorate.exception.NotFoundException;
import ru.practicum.filmorate.exception.ValidationException;
import ru.practicum.filmorate.model.User;
import ru.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final UserStorage storage;
    private final Validator validator;

    @Autowired
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

    public User findById(Integer id) {
        return storage.findById(id).orElseThrow(() -> new NotFoundException("User id=" + id + " not found"));
    }

    public void addFriend(Integer id, Integer friendId) {
        if (id.equals(friendId)) throw new ValidationException("Cannot add yourself to friends");
        User u = findById(id);
        User f = findById(friendId);
        boolean added1 = u.getFriends().add(friendId);
        boolean added2 = f.getFriends().add(id);
        log.info("Add friend: {} <-> {} (added1={}, added2={})", id, friendId, added1, added2);
    }

    public List<User> listFriends(Integer id) {
        User u = findById(id);
        return u.getFriends().stream().map(this::findById).toList();
    }

    public void removeFriend(Integer id, Integer friendId) {
        User u = findById(id);
        User f = findById(friendId);
        boolean r1 = u.getFriends().remove(friendId);
        boolean r2 = f.getFriends().remove(id);
        log.info("Remove friend: {} x {} (r1={}, r2={})", id, friendId, r1, r2);
    }

    public List<User> commonFriends(Integer id, Integer otherId) {
        User u1 = findById(id);
        User u2 = findById(otherId);
        return u1.getFriends().stream().filter(u2.getFriends()::contains).map(this::findById).toList();
    }

    private void normalize(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void validateBean(User user) {
        Set<ConstraintViolation<User>> v = validator.validate(user);
        if (!v.isEmpty()) throw new ValidationException(v.iterator().next().getMessage());
    }
}
