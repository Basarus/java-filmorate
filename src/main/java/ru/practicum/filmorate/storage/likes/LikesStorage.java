package ru.practicum.filmorate.storage.likes;

public interface LikesStorage {
    void like(int filmId, int userId);
    void unlike(int filmId, int userId);
}