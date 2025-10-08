package ru.practicum.filmorate.storage.mpa;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("!test")
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbc;
    private static final RowMapper<Mpa> M = (rs, n) -> new Mpa(rs.getInt("id"), rs.getString("name"));

    public MpaDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Mpa> findAll() {
        return jdbc.query("SELECT id, name FROM mpa ORDER BY id", M);
    }

    @Override
    public Optional<Mpa> findById(int id) {
        var list = jdbc.query("SELECT id, name FROM mpa WHERE id = ?", M, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    @Override
    public boolean exists(int id) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM mpa WHERE id = ?", Integer.class, id);
        return count != null && count > 0;
    }
}
