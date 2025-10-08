package ru.practicum.filmorate.service;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.filmorate.exception.NotFoundException;
import ru.practicum.filmorate.model.User;
import ru.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.practicum.filmorate.storage.friendship.InMemoryFriendshipStorage;
import ru.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserStorage users;
    private final FriendshipStorage friendships;

    @Autowired
    public UserService(UserStorage userStorage, FriendshipStorage friendships) {
        this.users = userStorage;
        this.friendships = friendships;
    }

    public User create(User u) throws BadRequestException {
        if (u == null) {
            throw new BadRequestException("User data must not be null");
        }
        if (u.getLogin() == null || u.getLogin().isBlank()) {
            throw new BadRequestException("Login cannot be empty");
        }
        if (u.getEmail() == null || u.getEmail().isBlank() || !u.getEmail().contains("@")) {
            throw new BadRequestException("Invalid email format");
        }
        if (u.getBirthday() != null && u.getBirthday().isAfter(java.time.LocalDate.now())) {
            throw new BadRequestException("Birthday cannot be in the future");
        }
        if (u.getName() == null || u.getName().isBlank()) {
            u.setName(u.getLogin());
        }
        try {
            User saved = users.save(u);
            if (friendships instanceof InMemoryFriendshipStorage m) {
                m.addUserToIndex(saved);
            }
            return saved;
        } catch (Exception e) {
            throw new InternalError("Failed to create user", e);
        }
    }

    public User update(User u) throws BadRequestException {
        if (u == null || u.getId() == null || u.getId() <= 0) {
            throw new BadRequestException("User ID must be positive for update");
        }
        exists(u.getId());
        if (u.getLogin() == null || u.getLogin().isBlank()) {
            throw new BadRequestException("Login cannot be empty");
        }
        if (u.getEmail() == null || u.getEmail().isBlank() || !u.getEmail().contains("@")) {
            throw new BadRequestException("Invalid email format");
        }
        if (u.getBirthday() != null && u.getBirthday().isAfter(java.time.LocalDate.now())) {
            throw new BadRequestException("Birthday cannot be in the future");
        }
        if (u.getName() == null || u.getName().isBlank()) {
            u.setName(u.getLogin());
        }
        try {
            User updated = users.update(u);
            if (friendships instanceof InMemoryFriendshipStorage m) {
                m.addUserToIndex(updated);
            }
            return updated;
        } catch (Exception e) {
            throw new InternalError("Failed to update user", e);
        }
    }

    public List<User> findAll() {
        List<User> all = users.findAll();
        if (all == null || all.isEmpty()) {
            throw new NotFoundException("No users found");
        }
        return all;
    }

    public Optional<User> findById(int id) throws BadRequestException {
        if (id <= 0) {
            throw new BadRequestException("User ID must be positive");
        }
        var user = users.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found: id=" + id);
        }
        return user;
    }

    public void addFriend(int id, int friendId) throws BadRequestException {
        if (id <= 0 || friendId <= 0) {
            throw new BadRequestException("User IDs must be positive");
        }
        if (id == friendId) {
            throw new BadRequestException("Cannot add yourself as a friend");
        }
        exists(id);
        exists(friendId);
        try {
            friendships.add(id, friendId);
        } catch (Exception e) {
            throw new InternalError("Failed to add friend", e);
        }
    }

    public void removeFriend(int id, int friendId) throws BadRequestException {
        if (id <= 0 || friendId <= 0) {
            throw new BadRequestException("User IDs must be positive");
        }
        exists(id);
        exists(friendId);
        try {
            friendships.remove(id, friendId);
        } catch (Exception e) {
            throw new InternalError("Failed to remove friend", e);
        }
    }

    public List<User> friends(int id) throws BadRequestException {
        if (id <= 0) {
            throw new BadRequestException("User ID must be positive");
        }
        exists(id);
        try {
            return friendships.friendsOf(id);
        } catch (Exception e) {
            throw new InternalError("Failed to load friends", e);
        }
    }

    public List<User> common(int id, int otherId) throws BadRequestException {
        if (id <= 0 || otherId <= 0) {
            throw new BadRequestException("User IDs must be positive");
        }
        exists(id);
        exists(otherId);
        try {
            return friendships.commonFriends(id, otherId);
        } catch (Exception e) {
            throw new InternalError("Failed to load common friends", e);
        }
    }

    public boolean exists(int id) throws BadRequestException {
        if (id <= 0) {
            throw new BadRequestException("User ID must be positive");
        }
        if (!users.exists(id)) {
            throw new NotFoundException("User not found: id=" + id);
        }
        return true;
    }
}
