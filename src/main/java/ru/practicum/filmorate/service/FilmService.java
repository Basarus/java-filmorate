package ru.practicum.filmorate.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.filmorate.exception.NotFoundException;
import ru.practicum.filmorate.exception.ValidationException;
import ru.practicum.filmorate.model.Film;
import ru.practicum.filmorate.storage.film.FilmStorage;
import ru.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage storage;
    private final Validator validator;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage storage, Validator validator, UserStorage userStorage) {
        this.storage = storage;
        this.validator = validator;
        this.userStorage = userStorage;
    }

    public FilmService(FilmStorage storage) {
        this.storage = storage;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        this.userStorage = null;
    }

    public Film create(Film film) {
        validateBean(film);
        Film saved = storage.save(film);
        log.info("Film created: {} {}", saved.getId(), saved.getName());
        return saved;
    }

    public Film update(Film film) {
        if (film.getId() == null || !storage.exists(film.getId())) {
            throw new NotFoundException("Film id=" + film.getId() + " not found");
        }
        validateBean(film);
        Film updated = storage.update(film);
        log.info("Film updated: {} {}", updated.getId(), updated.getName());
        return updated;
    }

    public List<Film> findAll() {
        return storage.findAll();
    }

    public Film findById(Integer id) {
        return storage.findById(id).orElseThrow(() -> new NotFoundException("Film id=" + id + " not found"));
    }

    public void like(Integer filmId, Integer userId) {
        Film film = findById(filmId);
        if (userStorage == null || userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("User id=" + userId + " not found");
        }
        boolean added = film.getLikes().add(userId);
        log.info("Like: film={} user={} added={}", filmId, userId, added);
    }

    public void unlike(Integer filmId, Integer userId) {
        Film film = findById(filmId);
        if (!userStorage.exists(userId)) {
            throw new NotFoundException("User id=" + userId + " not found");
        }
        film.getLikes().remove(userId);
        log.info("Unlike: film={} user={}", filmId, userId);
    }

    public List<Film> getPopular(int count) {
        return storage.findAll().stream().sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed().thenComparing(Film::getId)).limit(count).toList();
    }

    private void validateBean(Film film) {
        Set<ConstraintViolation<Film>> v = validator.validate(film);
        if (!v.isEmpty()) throw new ValidationException(v.iterator().next().getMessage());
    }
}
