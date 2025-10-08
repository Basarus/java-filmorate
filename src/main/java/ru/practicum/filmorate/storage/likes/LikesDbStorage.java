package ru.practicum.filmorate.storage.likes;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!test")
public class LikesDbStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    public LikesDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void like(int filmId, int userId) {
        jdbcTemplate.update("MERGE INTO film_likes (film_id, user_id) KEY(film_id, user_id) VALUES (?, ?)", filmId, userId);
    }

    @Override
    public void unlike(int filmId, int userId) {
        jdbcTemplate.update("DELETE FROM film_likes WHERE film_id=? AND user_id=?", filmId, userId);
    }
}
