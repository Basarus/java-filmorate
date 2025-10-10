package ru.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.practicum.filmorate.model.Film;
import ru.practicum.filmorate.model.Genre;
import ru.practicum.filmorate.model.Mpa;

import java.util.*;

@Service
@RequiredArgsConstructor
@Profile("!test")
public class FilmQueryService {
    private final JdbcTemplate jdbc;

    private static final RowMapper<Film> F = (rs, n) -> {
        Film f = new Film();
        f.setId(rs.getInt("id"));
        f.setName(rs.getString("name"));
        f.setDescription(rs.getString("description"));
        f.setReleaseDate(rs.getDate("release_date").toLocalDate());
        f.setDuration(rs.getInt("duration"));
        f.setMpaId(rs.getInt("mpa_id"));
        return f;
    };

    public record FullFilm(Film film, Mpa mpa, List<Genre> genres) {
    }

    public FullFilm load(int filmId) {
        var filmData = jdbc.query("""
                SELECT f.*, m.name AS mpa_name
                FROM films f
                JOIN mpa m ON f.mpa_id = m.id
                WHERE f.id = ?
                """, (rs, n) -> {
            Film f = new Film();
            f.setId(rs.getInt("id"));
            f.setName(rs.getString("name"));
            f.setDescription(rs.getString("description"));
            f.setReleaseDate(rs.getDate("release_date").toLocalDate());
            f.setDuration(rs.getInt("duration"));
            f.setMpaId(rs.getInt("mpa_id"));
            Mpa mpa = new Mpa(f.getMpaId(), rs.getString("mpa_name"));
            return new Object[]{f, mpa};
        }, filmId).stream().findFirst().orElse(null);

        if (filmData == null) return null;

        Film film = (Film) filmData[0];
        Mpa mpa = (Mpa) filmData[1];

        List<Genre> genres = jdbc.query("""
                    SELECT g.* FROM film_genre fg
                    JOIN genres g ON g.id = fg.genre_id
                    WHERE fg.film_id = ?
                    ORDER BY g.id
                """, (rs, n) -> new Genre(rs.getInt("id"), rs.getString("name")), filmId);

        Set<Integer> likes = new HashSet<>(jdbc.query("""
                    SELECT user_id FROM film_likes WHERE film_id = ?
                """, (rs, n) -> rs.getInt("user_id"), filmId));

        film.getLikes().addAll(likes);
        return new FullFilm(film, mpa, genres);
    }

    public List<FullFilm> loadAll() {
        List<Film> films = jdbc.query("SELECT * FROM films ORDER BY id", F);
        if (films.isEmpty()) return List.of();

        Map<Integer, Mpa> mpaMap = new HashMap<>();
        jdbc.query("SELECT * FROM mpa", rs -> {
            mpaMap.put(rs.getInt("id"), new Mpa(rs.getInt("id"), rs.getString("name")));
            return null;
        });

        Map<Integer, List<Genre>> gmap = new HashMap<>();

        jdbc.query("""
                 SELECT fg.film_id, g.id, g.name FROM film_genre fg\s
                 JOIN genres g ON g.id = fg.genre_id
                \s""", rs -> {

            int filmId = rs.getInt(1);
            int genreId = rs.getInt(2);
            String name = rs.getString(3);
            gmap.computeIfAbsent(filmId, k -> new ArrayList<>()).add(new Genre(genreId, name));
            return null;
        });

        Map<Integer, Set<Integer>> lmap = new HashMap<>();
        jdbc.query("""
                SELECT film_id, user_id FROM film_likes
                """, rs -> {
            int filmId = rs.getInt(1);
            int userId = rs.getInt(2);
            lmap.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
            return null;
        });

        List<FullFilm> res = new ArrayList<>();

        for (Film f : films) {
            f.getLikes().addAll(lmap.getOrDefault(f.getId(), Set.of()));
            res.add(new FullFilm(f, mpaMap.get(f.getMpaId()), gmap.getOrDefault(f.getId(), List.of())));
        }

        return res;
    }

    public List<Film> top(int limit) {
        return jdbc.query("""
                    SELECT f.* FROM films f
                    LEFT JOIN film_likes fl ON fl.film_id = f.id
                    GROUP BY f.id
                    ORDER BY COUNT(fl.user_id) DESC, f.id ASC
                    FETCH FIRST ? ROWS ONLY
                """, F, limit);
    }
}