package ru.practicum.filmorate.storage.film;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.practicum.filmorate.model.Film;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Profile("!test")
public class DbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbc;

    public DbFilmStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static Film mapFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setMpaId(rs.getInt("mpa_id"));
        return film;
    }

    private static final RowMapper<Film> FILM_MAPPER = (rs, rowNum) -> mapFilm(rs);

    @Override
    public Film save(Film film) {
        jdbc.update("""
                            INSERT INTO films (name, description, release_date, duration, mpa_id)
                            VALUES (?, ?, ?, ?, ?)
                        """,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaId()
        );

        int id = jdbc.queryForObject("SELECT MAX(id) FROM films", Integer.class);
        film.setId(id);

        if (film.getGenreIds() != null && !film.getGenreIds().isEmpty()) {
            Set<Integer> genreIds = film.getGenreIds();
            jdbc.batchUpdate(
                    "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                    genreIds,
                    genreIds.size(),
                    (ps, genreId) -> {
                        ps.setInt(1, id);
                        ps.setInt(2, genreId);
                    }
            );
        }

        return film;
    }

    @Override
    public Film update(Film film) {
        jdbc.update("""
                        UPDATE films 
                        SET name=?, description=?, release_date=?, duration=?, mpa_id=? 
                        WHERE id=?
                        """,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpaId(),
                film.getId()
        );

        jdbc.update("DELETE FROM film_genre WHERE film_id=?", film.getId());

        Set<Integer> genreIds = film.getGenreIds();
        if (genreIds != null && !genreIds.isEmpty()) {
            jdbc.batchUpdate(
                    "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                    genreIds,
                    genreIds.size(),
                    (ps, genreId) -> {
                        ps.setInt(1, film.getId());
                        ps.setInt(2, genreId);
                    }
            );
        }

        return film;
    }

    @Override
    public List<Film> findAll() {
        return jdbc.query("SELECT * FROM films", FILM_MAPPER);
    }

    @Override
    public Optional<Film> findById(int id) {
        return jdbc.query("SELECT * FROM films WHERE id=?", FILM_MAPPER, id)
                .stream()
                .findFirst();
    }

    @Override
    public boolean exists(int id) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM films WHERE id=?", Integer.class, id);
        return count != null && count > 0;
    }
}
