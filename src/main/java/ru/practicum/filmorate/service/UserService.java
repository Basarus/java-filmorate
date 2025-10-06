package ru.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.filmorate.exception.NotFoundException;
import ru.practicum.filmorate.exception.ValidationException;
import ru.practicum.filmorate.model.User;
import ru.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public User findById(Integer id) {
        return storage.findById(id).orElseThrow(() -> new NotFoundException("User id=" + id + " not found"));
    }

    public void addFriend(Integer id, Integer friendId) {
        if (id.equals(friendId)) throw new ValidationException("Cannot add yourself to friends");
        User a = findById(id);
        User b = findById(friendId);
        boolean added1 = a.getFriends().add(b.getId());
        boolean added2 = b.getFriends().add(a.getId());
        log.info("Add friend: {} <-> {} (added1={}, added2={})", id, friendId, added1, added2);
    }

    public void removeFriend(Integer id, Integer friendId) {
        User a = findById(id);
        User b = findById(friendId);
        boolean r1 = a.getFriends().remove(b.getId());
        boolean r2 = b.getFriends().remove(a.getId());
        log.info("Remove friend: {} x {} (r1={}, r2={})", id, friendId, r1, r2);
    }

    public List<User> listFriends(Integer id) {
        User u = findById(id);
        Set<Integer> ids = u.getFriends();
        return ids.stream().map(this::findById).collect(Collectors.toList());
    }

    public List<User> commonFriends(Integer id, Integer otherId) {
        User a = findById(id);
        User b = findById(otherId);
        return a.getFriends().stream().filter(b.getFriends()::contains).map(this::findById).collect(Collectors.toList());
    }

    private void normalize(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
