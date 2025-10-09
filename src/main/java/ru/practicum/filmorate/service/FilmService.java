package ru.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.filmorate.model.Film;
import ru.practicum.filmorate.storage.film.FilmStorage;
import ru.practicum.filmorate.storage.genre.GenreStorage;
import ru.practicum.filmorate.storage.likes.LikesStorage;
import ru.practicum.filmorate.storage.mpa.MpaStorage;
import ru.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;

@Service
public class FilmService {

    private final FilmStorage films;
    private final UserStorage users;
    private final FilmQueryService filmQueryService;
    private final LikesStorage likes;
    private final MpaStorage mpaDao;
    private final GenreStorage genreDao;

    @Autowired
    public FilmService(FilmStorage films, UserStorage users, FilmQueryService filmQueryService, LikesStorage likes, MpaStorage mpaDao, GenreStorage genreDao) {
        this.films = films;
        this.users = users;
        this.filmQueryService = filmQueryService;
        this.likes = likes;
        this.mpaDao = mpaDao;
        this.genreDao = genreDao;
    }

    public Film create(Film film) {
        if (film == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Film data must not be null");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Film name cannot be empty");
        }
        if (film.getDuration() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Film duration must be positive");
        }
        validateRefs(film);
        Film saved = films.save(film);
        FilmQueryService.FullFilm full = filmQueryService.load(saved.getId());

        if (full == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found after creation");
        }
        return full.film();
    }

    public Film update(Film f) {
        if (f == null || f.getId() == null || f.getId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Film ID must be positive for update");
        }
        existsFilm(f.getId());
        if (f.getName() == null || f.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Film name cannot be empty");
        }
        if (f.getDuration() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Film duration must be positive");
        }
        validateRefs(f);
        return films.update(f);
    }

    public List<Film> findAll() {
        List<Film> all = films.findAll();
        if (all == null || all.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No films found");
        }
        return all;
    }

    public Film findById(int id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Film ID must be positive");
        }
        return films.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found"));
    }

    public void addLike(int filmId, int userId) {
        if (filmId <= 0 || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Film ID and User ID must be positive");
        }
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
        if (filmId <= 0 || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Film ID and User ID must be positive");
        }
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
        if (count <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Count must be positive");
        }

        List<Film> all = films.findAll();
        if (all == null || all.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No films found");
        }

        return all.stream().sorted((f1, f2) -> {
            int likes1 = getLikesCount(f1.getId());
            int likes2 = getLikesCount(f2.getId());
            return Integer.compare(likes2, likes1);
        }).limit(count).toList();
    }

    private int getLikesCount(int filmId) {
        if (likes == null) {
            return findById(filmId).getLikes().size();
        }
        return likes.countLikes(filmId);
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
        Integer mpaId = f.getMpaId();

        if (mpaId != null) {
            if (mpaDao == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "MPA storage not initialized");
            }
            boolean exists = mpaDao.exists(mpaId);
            if (!exists) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "MPA with id=" + mpaId + " not found");
            }
        }

        Set<Integer> genreIds = f.getGenreIds();

        if (genreIds != null && !genreIds.isEmpty()) {
            if (genreDao == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Genre storage not initialized");
            }
            boolean allExist = genreDao.allExist(genreIds);
            if (!allExist) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One or more genres not found");
            }
        }
    }
}
