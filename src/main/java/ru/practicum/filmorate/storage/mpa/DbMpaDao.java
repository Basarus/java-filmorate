package ru.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.practicum.filmorate.dao.MpaDao;
import ru.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DbMpaDao implements MpaDao {
    private final JdbcTemplate jdbc;

    private static final RowMapper<Mpa> MAPPER = (rs, n) -> new Mpa(rs.getInt("id"), rs.getString("name"));

    @Override
    public List<Mpa> findAll() {
        return jdbc.query("SELECT * FROM mpa ORDER BY id", MAPPER);
    }

    @Override
    public Optional<Mpa> findById(int id) {
        return jdbc.query("SELECT * FROM mpa WHERE id = ?", MAPPER, id).stream().findFirst();
    }

    @Override
    public boolean exists(int id) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM mpa WHERE id = ?", Integer.class, id);
        return count != null && count > 0;
    }
}

