package ru.practicum.filmorate.dao;

public interface LikesDao {
    void like(int filmId, int userId);

    void unlike(int filmId, int userId);
}