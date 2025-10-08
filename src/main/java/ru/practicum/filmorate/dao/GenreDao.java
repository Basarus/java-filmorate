package ru.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
@Profile("!test")
public class GenreDao {
    protected final JdbcTemplate jdbc;

    private static final RowMapper<Genre> G = (rs, n) -> new Genre(rs.getInt("id"), rs.getString("name"));

    public List<Genre> findAll() {
        return jdbc.query("SELECT * FROM genre ORDER BY id", G);
    }

    public Optional<Genre> findById(int id) {
        return jdbc.query("SELECT * FROM genre WHERE id = ?", G, id).stream().findFirst();
    }

    public boolean exists(int id) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM genre WHERE id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public boolean allExist(Set<Integer> ids) {
        if (ids == null || ids.isEmpty()) return true;
        String placeholders = ids.stream().map(id -> "?").collect(Collectors.joining(","));
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM genre WHERE id IN (" + placeholders + ")", Integer.class, ids.toArray());
        return count != null && count == ids.size();
    }
}
