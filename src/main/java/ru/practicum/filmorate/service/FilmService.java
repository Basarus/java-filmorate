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
        Film saved = storage.save(film);
        log.info("Film created: {} {}", saved.getId(), saved.getName());
        return saved;
    }

    public Film update(Film film) {
        if (film.getId() == null || !storage.exists(film.getId())) {
            throw new NotFoundException("Film id=" + film.getId() + " not found");
        }
        Film updated = storage.update(film);
        log.info("Film updated: {} {}", updated.getId(), updated.getName());
        return updated;
    }

    public List<Film> findAll() {
        return storage.findAll();
    }
}
