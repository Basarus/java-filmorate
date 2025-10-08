package ru.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class GenreDao {
    private final JdbcTemplate jdbc;

    private static final RowMapper<Genre> G = (rs, n) -> new Genre(rs.getInt("id"), rs.getString("name"));

    public List<Genre> findAll() {
        return jdbc.query("SELECT * FROM genre ORDER BY id", G);
    }

    public boolean allExist(Set<Integer> ids) {
        if (ids == null || ids.isEmpty()) return true;
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM genre WHERE id IN (" + String.join(",", ids.stream().map(String::valueOf).toList()) + ")", Integer.class);
        return count != null && count == ids.size();
    }
}