package ru.practicum.filmorate.storage.likes;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
@Profile("!test")
public class LikesDbStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    public LikesDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void like(int filmId, int userId) {
        jdbcTemplate.update(
                "MERGE INTO film_likes (film_id, user_id) KEY(film_id, user_id) VALUES (?, ?)",
                filmId, userId
        );
    }

    @Override
    public void unlike(int filmId, int userId) {
        jdbcTemplate.update(
                "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?",
                filmId, userId
        );
    }

    @Override
    public int countLikes(int filmId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_likes WHERE film_id = ?",
                Integer.class,
                filmId
        );
        return count != null ? count : 0;
    }

    @Override
    public Set<Integer> getLikes(int filmId) {
        return new HashSet<>(jdbcTemplate.query("""
                    SELECT user_id FROM film_likes WHERE film_id = ?
                """, (rs, n) -> rs.getInt("user_id"), filmId));
    }
}
