package ru.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.filmorate.exception.NotFoundException;
import ru.practicum.filmorate.model.Film;
import ru.practicum.filmorate.storage.film.FilmStorage;
import ru.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage storage;
    private final UserStorage userStorage;

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

    public Film findById(Integer id) {
        return storage.findById(id).orElseThrow(() -> new NotFoundException("Film id=" + id + " not found"));
    }

    public void like(Integer filmId, Integer userId) {
        Film film = findById(filmId);
        if (!userStorage.exists(userId)) {
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
        boolean removed = film.getLikes().remove(userId);
        log.info("Unlike: film={} user={} removed={}", filmId, userId, removed);
    }

    public List<Film> getPopular(int count) {
        return storage.findAll().stream()
                .sorted(Comparator.<Film>comparingInt(f -> -f.getLikes().size())
                        .thenComparingInt(Film::getId))
                .limit(count)
                .toList();
    }
}
