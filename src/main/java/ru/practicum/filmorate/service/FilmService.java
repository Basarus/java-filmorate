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
import ru.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage storage;
    private final Validator validator;

    @Autowired
    public FilmService(FilmStorage storage, Validator validator) {
        this.storage = storage;
        this.validator = validator;
    }

    public FilmService(FilmStorage storage) {
        this.storage = storage;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
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

    private void validateBean(Film film) {
        Set<ConstraintViolation<Film>> v = validator.validate(film);
        if (!v.isEmpty()) throw new ValidationException(v.iterator().next().getMessage());
    }
}
