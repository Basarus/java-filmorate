package ru.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.practicum.filmorate.dao.GenreDao;
import ru.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class DbGenreDao implements GenreDao {
    private final JdbcTemplate jdbc;

    private static final RowMapper<Genre> M = (rs, n) -> new Genre(rs.getInt("id"), rs.getString("name"));

    @Override
    public List<Genre> findAll() {
        return jdbc.query("SELECT * FROM genres ORDER BY id", M);
    }

    @Override
    public Optional<Genre> findById(int id) {
        return jdbc.query("SELECT * FROM genres WHERE id=?", M, id).stream().findFirst();
    }

    @Override
    public boolean exists(int id) {
        Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM genres WHERE id=?", Integer.class, id);
        return c != null && c > 0;
    }

    @Override
    public boolean allExist(Set<Integer> ids) {
        if (ids == null || ids.isEmpty()) return true;
        String in = String.join(",", java.util.Collections.nCopies(ids.size(), "?"));
        Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM genres WHERE id IN (" + in + ")", Integer.class, ids.toArray());
        return c != null && c == ids.size();
    }
}