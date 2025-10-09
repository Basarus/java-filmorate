package ru.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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

    public User create(User u) {
        if (u == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User data must not be null");
        }
        if (u.getLogin() == null || u.getLogin().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Login cannot be empty");
        }
        if (u.getEmail() == null || u.getEmail().isBlank() || !u.getEmail().contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
        }
        if (u.getBirthday() != null && u.getBirthday().isAfter(java.time.LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Birthday cannot be in the future");
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
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create user", e);
        }
    }

    public User update(User u) {
        if (u == null || u.getId() == null || u.getId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID must be positive for update");
        }
        exists(u.getId());
        if (u.getLogin() == null || u.getLogin().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Login cannot be empty");
        }
        if (u.getEmail() == null || u.getEmail().isBlank() || !u.getEmail().contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
        }
        if (u.getBirthday() != null && u.getBirthday().isAfter(java.time.LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Birthday cannot be in the future");
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
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update user", e);
        }
    }

    public List<User> findAll() {
        try {
            List<User> all = users.findAll();
            if (all == null || all.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No users found");
            }
            return all;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to load users", e);
        }
    }

    public Optional<User> findById(int id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID must be positive");
        }
        try {
            var user = users.findById(id);
            if (user.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: id=" + id);
            }
            return user;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to load user", e);
        }
    }

    public void addFriend(int id, int friendId) {
        if (id <= 0 || friendId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User IDs must be positive");
        }
        if (id == friendId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot add yourself as a friend");
        }
        exists(id);
        exists(friendId);
        try {
            friendships.add(id, friendId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add friend", e);
        }
    }

    public void removeFriend(int id, int friendId) {
        if (id <= 0 || friendId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User IDs must be positive");
        }
        exists(id);
        exists(friendId);
        try {
            friendships.remove(id, friendId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to remove friend", e);
        }
    }

    public List<User> friends(int id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID must be positive");
        }
        exists(id);
        try {
            return friendships.friendsOf(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to load friends", e);
        }
    }

    public List<User> common(int id, int otherId) {
        if (id <= 0 || otherId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User IDs must be positive");
        }
        exists(id);
        exists(otherId);
        try {
            return friendships.commonFriends(id, otherId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to load common friends", e);
        }
    }

    public boolean exists(int id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID must be positive");
        }
        if (!users.exists(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: id=" + id);
        }
        return true;
    }
}
