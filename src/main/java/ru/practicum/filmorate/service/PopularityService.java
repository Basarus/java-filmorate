package ru.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.practicum.filmorate.model.Film;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PopularityService {
    private final JdbcTemplate jdbc;
    private static final RowMapper<Film> M = (rs, n) -> {
        Film f = new Film();
        f.setId(rs.getInt("id"));
        f.setName(rs.getString("name"));
        f.setDescription(rs.getString("description"));
        f.setReleaseDate(rs.getDate("release_date").toLocalDate());
        f.setDuration(rs.getInt("duration"));
        f.setMpaId(rs.getInt("mpa_id"));
        return f;
    };

    public List<Film> top(int limit) {
        return jdbc.query("SELECT f.* FROM films f LEFT JOIN film_likes fl ON fl.film_id=f.id " + "GROUP BY f.id ORDER BY COUNT(fl.user_id) DESC, f.id ASC FETCH FIRST ? ROWS ONLY", M, limit);
    }
}
