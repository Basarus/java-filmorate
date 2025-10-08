package ru.practicum.filmorate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.filmorate.dao.FriendshipDao;
import ru.practicum.filmorate.dao.InMemoryFriendshipDao;
import ru.practicum.filmorate.exception.NotFoundException;
import ru.practicum.filmorate.exception.ValidationException;
import ru.practicum.filmorate.model.User;
import ru.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage users;
    private final FriendshipDao friendships;

    public UserService(UserStorage userStorage, FriendshipDao friendshipDao) {
        this.users = userStorage;
        this.friendships = friendshipDao;
    }

    public User create(User u) {
        if (u.getName() == null || u.getName().isBlank()) u.setName(u.getLogin());
        User saved = users.save(u);
        if (friendships instanceof InMemoryFriendshipDao m) m.addUserToIndex(saved);
        return saved;
    }

    public User update(User u) {
        exists(u.getId());
        if (u.getName() == null || u.getName().isBlank()) u.setName(u.getLogin());
        User updated = users.update(u);
        if (friendships instanceof InMemoryFriendshipDao m) m.addUserToIndex(updated);
        return updated;
    }

    public List<User> findAll() {
        return users.findAll();
    }

    public User findById(int id) {
        return users.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: id=" + id));
    }

    public void addFriend(int id, int friendId) {
        if (id == friendId) throw new ValidationException("Нельзя добавить в друзья самого себя");
        exists(id);
        exists(friendId);
        friendships.add(id, friendId);
    }

    public void removeFriend(int id, int friendId) {
        exists(id);
        exists(friendId);
        friendships.remove(id, friendId);
    }

    public List<User> friends(int id) {
        exists(id);
        return friendships.friendsOf(id);
    }

    public List<User> common(int id, int otherId) {
        exists(id);
        exists(otherId);
        return friendships.commonFriends(id, otherId);
    }

    public List<User> listFriends(int id) {
        return friends(id);
    }

    public List<User> commonFriends(int id, int otherId) {
        return common(id, otherId);
    }

    private void exists(int id) {
        if (!users.exists(id)) throw new NotFoundException("Пользователь не найден");
    }
}
