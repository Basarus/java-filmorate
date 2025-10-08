package ru.practicum.filmorate.storage.likes;

import java.util.Set;

public interface LikesStorage {
    void like(int filmId, int userId);

    void unlike(int filmId, int userId);

    int countLikes(int filmId);

    Set<Integer> getLikes(int filmId);
}