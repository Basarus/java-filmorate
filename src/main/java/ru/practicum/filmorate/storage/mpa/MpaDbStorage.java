package ru.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
@Profile("!test")
public class MpaDbStorage implements MpaStorage {
    protected final JdbcTemplate jdbc;

    private static final RowMapper<Mpa> M = (rs, n) -> new Mpa(rs.getInt("id"), rs.getString("name"));

    public List<Mpa> findAll() {
        return jdbc.query("SELECT * FROM mpa ORDER BY id", M);
    }

    public Optional<Mpa> findById(int id) {
        return jdbc.query("SELECT * FROM mpa WHERE id = ?", M, id).stream().findFirst();
    }

    public boolean exists(int id) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM mpa WHERE id = ?", Integer.class, id);
        return count != null && count > 0;
    }
}
