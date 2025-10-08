package ru.practicum.filmorate.storage.friendship;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.practicum.filmorate.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("test")
public class InMemoryFriendshipStorage implements FriendshipStorage {
    private final Map<Integer, Set<Integer>> links = new ConcurrentHashMap<>();
    private final Map<Integer, User> usersIndex = new ConcurrentHashMap<>();

    public void addUserToIndex(User u) {
        if (u != null && u.getId() != null) usersIndex.put(u.getId(), u);
    }

    @Override
    public void add(int userId, int friendId) {
        links.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
    }

    @Override
    public void remove(int userId, int friendId) {
        links.getOrDefault(userId, Collections.emptySet()).remove(friendId);
    }

    @Override
    public List<User> friendsOf(int userId) {
        Set<Integer> ids = links.getOrDefault(userId, Collections.emptySet());
        List<User> list = new ArrayList<>();
        for (Integer id : ids) {
            User u = usersIndex.get(id);
            if (u != null) list.add(u);
        }
        list.sort(Comparator.comparingInt(User::getId));
        return list;
    }

    @Override
    public List<User> commonFriends(int userA, int userB) {
        Set<Integer> a = new HashSet<>(links.getOrDefault(userA, Collections.emptySet()));
        a.retainAll(links.getOrDefault(userB, Collections.emptySet()));
        List<User> list = new ArrayList<>();
        for (Integer id : a) {
            User u = usersIndex.get(id);
            if (u != null) list.add(u);
        }
        list.sort(Comparator.comparingInt(User::getId));
        return list;
    }
}
