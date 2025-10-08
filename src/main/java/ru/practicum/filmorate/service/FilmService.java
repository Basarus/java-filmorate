package ru.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.filmorate.model.Film;
import ru.practicum.filmorate.storage.film.FilmStorage;
import ru.practicum.filmorate.storage.genre.GenreStorage;
import ru.practicum.filmorate.storage.genre.InMemoryGenreStorage;
import ru.practicum.filmorate.storage.likes.InMemoryLikesStorage;
import ru.practicum.filmorate.storage.likes.LikesStorage;
import ru.practicum.filmorate.storage.mpa.InMemoryMpaStorage;
import ru.practicum.filmorate.storage.mpa.MpaStorage;
import ru.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class FilmService {

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    private final FilmStorage films;
    private final UserStorage users;
    private final FilmQueryService filmQueryService;
    private LikesStorage likes;
    private PopularityService popularity;
    private MpaStorage mpaDao;
    private GenreStorage genreDao;

    public FilmService(FilmStorage films, UserStorage users, FilmQueryService filmQueryService) {
        this.films = films;
        this.users = users;
        this.filmQueryService = filmQueryService;
    }

    @Autowired(required = false)
    public void setLikes(@Nullable LikesStorage likes) {
        this.likes = likes != null ? likes : new InMemoryLikesStorage();
    }

    @Autowired(required = false)
    public void setPopularity(@Nullable PopularityService popularity) {
        this.popularity = popularity;
    }

    @Autowired(required = false)
    public void setMpaDao(@Nullable MpaStorage mpaDao) {
        this.mpaDao = mpaDao != null ? mpaDao : new InMemoryMpaStorage();
    }

    @Autowired(required = false)
    public void setGenreDao(@Nullable GenreStorage genreDao) {
        this.genreDao = genreDao != null ? genreDao : new InMemoryGenreStorage();
    }

    public Film create(Film film) {
        validateRefs(film);
        Film saved = films.save(film);
        FilmQueryService.FullFilm full = filmQueryService.load(saved.getId());
        if (full == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found after creation");
        }
        return full.film();
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
        return films.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found"));
    }

    public void addLike(int filmId, int userId) {
        existsFilm(filmId);
        existsUser(userId);
        if (likes == null) {
            Film f = findById(filmId);
            f.getLikes().add(userId);
            return;
        }
        likes.like(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        existsFilm(filmId);
        existsUser(userId);
        if (likes == null) {
            Film f = findById(filmId);
            f.getLikes().remove(userId);
            return;
        }
        likes.unlike(filmId, userId);
    }

    public List<Film> top(int count) {
        if (popularity != null) return popularity.top(count);
        return films.findAll().stream().sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size())).limit(count).toList();
    }

    private void existsFilm(int id) {
        if (!films.exists(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found");
        }
    }

    private void existsUser(int id) {
        if (!users.exists(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    private void validateRefs(Film f) {
        if (f.getReleaseDate() != null && f.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Release date cannot be before December 28, 1895");
        }

        Integer mpaId = f.getMpaId();
        if (mpaId != null) {
            try {
                if (!mpaDao.exists(mpaId)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "MPA with id=" + mpaId + " not found");
                }
            } catch (Exception ignored) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One or more genres not found");
            }
        }

        Set<Integer> genreIds = f.getGenreIds();
        if (genreIds != null && !genreIds.isEmpty()) {
            try {
                if (!genreDao.allExist(genreIds)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One or more genres not found");
                }
            } catch (Exception ignored) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One or more genres not found");
            }
        }
    }
}
