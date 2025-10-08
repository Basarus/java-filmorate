package ru.practicum.filmorate.storage.friendship;

import ru.practicum.filmorate.model.User;

import java.util.List;

public interface FriendshipStorage {
    void add(int userId, int friendId);
    void remove(int userId, int friendId);
    List<User> friendsOf(int userId);
    List<User> commonFriends(int userA, int userB);
}
