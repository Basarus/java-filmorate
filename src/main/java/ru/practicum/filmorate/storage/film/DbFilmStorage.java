package ru.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.sql.Date;

@Repository
@RequiredArgsConstructor
public class DbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbc;

    @Override
    public Film save(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpaId());
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().intValue());

        if (film.getGenreIds() != null) {
            for (Integer gid : film.getGenreIds()) {
                jdbc.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)", film.getId(), gid);
            }
        }

        return film;
    }

    @Override
    public Film update(Film film) {
        jdbc.update("UPDATE films SET name=?, description=?, release_date=?, duration=?, mpa_id=? WHERE id=?",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpaId(),
                film.getId()
        );

        jdbc.update("DELETE FROM film_genre WHERE film_id=?", film.getId());
        for (Integer gid : film.getGenreIds()) {
            jdbc.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)", film.getId(), gid);
        }

        return film;
    }

    @Override
    public List<Film> findAll() {
        return jdbc.query("SELECT * FROM films", (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setMpaId(rs.getInt("mpa_id"));
            return film;
        });
    }

    @Override
    public Optional<Film> findById(int id) {
        List<Film> films = jdbc.query("SELECT * FROM films WHERE id=?", (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setMpaId(rs.getInt("mpa_id"));
            return film;
        }, id);
        return films.stream().findFirst();
    }

    @Override
    public boolean exists(int id) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM films WHERE id=?", Integer.class, id);
        return count != null && count > 0;
    }
}
