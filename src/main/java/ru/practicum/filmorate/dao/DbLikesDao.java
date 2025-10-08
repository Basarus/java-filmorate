package ru.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DbLikesDao implements LikesDao {
    private final JdbcTemplate jdbc;

    @Override
    public void like(int filmId, int userId) {
        jdbc.update(
                "MERGE INTO film_likes(film_id, user_id) KEY(film_id, user_id) VALUES (?, ?)",
                filmId,
                userId
        );
    }

    @Override
    public void unlike(int filmId, int userId) {
        jdbc.update("DELETE FROM film_likes WHERE film_id = ? AND user_id = ?", filmId, userId);
    }
}
