package ru.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.filmorate.exception.NotFoundException;
import ru.practicum.filmorate.exception.ValidationException;
import ru.practicum.filmorate.model.Film;
import ru.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private final FilmStorage storage;

    public Film create(Film film) {
        validate(film);
        Film saved = storage.save(film);
        log.info("Film created: {} {}", saved.getId(), saved.getName());
        return saved;
    }

    public Film update(Film film) {
        if (film.getId() == null || !storage.exists(film.getId())) {
            throw new NotFoundException("Film id=" + film.getId() + " not found");
        }
        validate(film);
        Film updated = storage.update(film);
        log.info("Film updated: {} {}", updated.getId(), updated.getName());
        return updated;
    }

    public List<Film> findAll() {
        return storage.findAll();
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("name must not be blank");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("description length must be <= 200");
        }
        if (film.getReleaseDate() == null) {
            throw new ValidationException("releaseDate must not be null");
        }
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("releaseDate must be on or after 1895-12-28");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("duration must be positive");
        }
    }
}
