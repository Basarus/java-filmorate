package ru.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.filmorate.storage.genre.GenreStorage;
import ru.practicum.filmorate.storage.likes.LikesDbStorage;
import ru.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.practicum.filmorate.model.Film;
import ru.practicum.filmorate.storage.film.FilmStorage;
import ru.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class FilmService {

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    private final FilmStorage films;
    private final UserStorage users;

    private LikesDbStorage likes;
    private PopularityService popularity;
    private MpaDbStorage mpaDao;
    private GenreStorage genreDao;

    public FilmService(FilmStorage films, UserStorage users) {
        this.films = films;
        this.users = users;
    }

    @Autowired(required = false)
    public void setLikes(@Nullable LikesDbStorage likes) {
        this.likes = likes;
    }

    @Autowired(required = false)
    public void setPopularity(@Nullable PopularityService popularity) {
        this.popularity = popularity;
    }

    @Autowired(required = false)
    public void setMpaDao(@Nullable MpaDbStorage mpaDao) {
        this.mpaDao = mpaDao;
    }

    @Autowired(required = false)
    public void setGenreDao(@Nullable GenreStorage genreDao) {
        this.genreDao = genreDao;
    }

    public Film create(Film f) {
        validateRefs(f);
        Film saved = films.save(f);
        return films.findById(saved.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found after creation"));
    }

    public Film update(Film f) {
        existsFilm(f.getId());
        validateRefs(f);
        return films.update(f);
    }

    public List<Film> findAll() {
        return films.findAll();
    }

    public Film findById(int id) {
        return films.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public void addLike(int filmId, int userId) {
        existsFilm(filmId);
        existsUser(userId);
        if (likes != null) {
            likes.like(filmId, userId);
        } else {
            Film f = findById(filmId);
            f.getLikes().add(userId);
        }
    }

    public void removeLike(int filmId, int userId) {
        existsFilm(filmId);
        existsUser(userId);
        if (likes != null) {
            likes.unlike(filmId, userId);
        } else {
            Film f = findById(filmId);
            f.getLikes().remove(userId);
        }
    }

    public List<Film> top(int count) {
        if (popularity != null) return popularity.top(count);
        return films.findAll().stream().sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size())).limit(count).toList();
    }

    private void existsFilm(int id) {
        if (!films.exists(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    private void existsUser(int id) {
        if (!users.exists(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    private void validateRefs(Film f) {
        if (f.getReleaseDate() != null && f.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Release date cannot be before December 28, 1895");
        }

        if (mpaDao != null) {
            Integer mpaId = f.getMpaId();
            if (mpaId == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "MPA must be specified");
            }

            boolean exists;
            try {
                exists = mpaDao.exists(mpaId);
            } catch (Exception e) {
                exists = false;
            }

            if (!exists) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "MPA with id=" + mpaId + " not found");
            }
        }

        if (genreDao != null) {
            Set<Integer> genreIds = f.getGenreIds();
            if (genreIds != null && !genreIds.isEmpty() && !genreDao.allExist(genreIds)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One or more genres not found");
            }
        }
    }
}
