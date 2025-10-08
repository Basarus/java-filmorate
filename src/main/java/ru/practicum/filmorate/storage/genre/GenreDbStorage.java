package ru.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Profile("!test")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbc;

    private static final RowMapper<ru.practicum.filmorate.model.Genre> G = (rs, n) -> new ru.practicum.filmorate.model.Genre(rs.getInt("id"), rs.getString("name"));

    @Override
    public List<ru.practicum.filmorate.model.Genre> findAll() {
        return jdbc.query("SELECT * FROM genre ORDER BY id", G);
    }

    @Override
    public Optional<ru.practicum.filmorate.model.Genre> findById(int id) {
        return jdbc.query("SELECT * FROM genre WHERE id = ?", G, id).stream().findFirst();
    }

    @Override
    public boolean exists(int id) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM genre WHERE id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public boolean allExist(Set<Integer> ids) {
        if (ids == null || ids.isEmpty()) return true;
        String placeholders = ids.stream().map(id -> "?").collect(Collectors.joining(","));
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM genre WHERE id IN (" + placeholders + ")", Integer.class, ids.toArray());
        return count != null && count == ids.size();
    }
}
