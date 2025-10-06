package ru.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.filmorate.dao.GenreDao;
import ru.practicum.filmorate.dao.LikesDao;
import ru.practicum.filmorate.dao.MpaDao;
import ru.practicum.filmorate.model.Film;
import ru.practicum.filmorate.storage.film.FilmStorage;
import ru.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;

@Service
public class FilmService {
    private final FilmStorage films;
    private final UserStorage users;

    private LikesDao likes;
    private PopularityService popularity;
    private MpaDao mpaDao;
    private GenreDao genreDao;

    public FilmService(FilmStorage films, UserStorage users) {
        this.films = films;
        this.users = users;
    }

    @Autowired(required = false)
    public void setLikes(@Nullable LikesDao likes) {
        this.likes = likes;
    }

    @Autowired(required = false)
    public void setPopularity(@Nullable PopularityService popularity) {
        this.popularity = popularity;
    }

    @Autowired(required = false)
    public void setMpaDao(@Nullable MpaDao mpaDao) {
        this.mpaDao = mpaDao;
    }

    @Autowired(required = false)
    public void setGenreDao(@Nullable GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public Film create(Film f) {
        validateRefs(f);
        Film saved = films.save(f);
        System.out.println("Saved film: " + saved);
        System.out.println("Saved ID: " + saved.getId());
        System.out.println("All films after save: " + films.findAll());
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
        if (mpaDao != null) {
            Integer mpaId = f.getMpaId();
            if (mpaId == null || !mpaDao.exists(mpaId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (genreDao != null) {
            Set<Integer> genres = f.getGenreIds();
            if (!genreDao.allExist(genres)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
