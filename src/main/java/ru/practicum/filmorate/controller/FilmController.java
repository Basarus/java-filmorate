package ru.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.filmorate.dto.*;
import ru.practicum.filmorate.model.Film;
import ru.practicum.filmorate.model.Mpa;
import ru.practicum.filmorate.service.FilmQueryService;
import ru.practicum.filmorate.service.FilmService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService service;
    private final FilmQueryService query;

    @PostMapping
    public FilmResponse create(@RequestBody @Valid FilmCreateRequest r) {
        log.info("Creating film: name={}, releaseDate={}, duration={}", r.getName(), r.getReleaseDate(), r.getDuration());
        Film f = new Film();
        f.setName(r.getName());
        f.setDescription(r.getDescription());
        f.setReleaseDate(r.getReleaseDate());
        f.setDuration(r.getDuration());
        f.setMpaId(r.getMpaId());
        f.setGenreIds(r.getGenreIds());
        Film created = service.create(f);
        log.info("Film created with id={}", created.getId());
        return map(created.getId());
    }

    @PutMapping
    public FilmResponse update(@RequestBody @Valid FilmUpdateRequest r) {
        log.info("Updating film id={}", r.getId());
        if (r.getId() == null || r.getId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Film id must be positive");
        }
        validateInput(r.getMpaId(), r.getGenreIds());
        Film f = new Film();
        f.setId(r.getId());
        f.setName(r.getName());
        f.setDescription(r.getDescription());
        f.setReleaseDate(r.getReleaseDate());
        f.setDuration(r.getDuration());
        f.setMpaId(r.getMpaId());
        f.setGenreIds(r.getGenreIds());
        Film updated = service.update(f);
        log.info("Film updated: id={}, name={}", updated.getId(), updated.getName());
        return map(updated.getId());
    }

    @GetMapping
    public List<FilmResponse> all() {
        return service.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/popular")
    public List<FilmResponse> popular(@RequestParam(defaultValue = "10") int count) {
        if (count <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Count must be positive");
        }
        return service.top(count).stream()
                .map(f -> map(f.getId()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id:\\d+}")
    public FilmResponse one(@PathVariable int id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Film id must be positive");
        }
        return map(id);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void like(@PathVariable int filmId, @PathVariable int userId) {
        if (filmId <= 0 || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Film and user ids must be positive");
        }
        service.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void unlike(@PathVariable int filmId, @PathVariable int userId) {
        if (filmId <= 0 || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Film and user ids must be positive");
        }
        service.removeLike(filmId, userId);
    }

    private FilmResponse map(int filmId) {
        var full = query.load(filmId);
        if (full == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found");
        }
        return new FilmResponse(
                full.film().getId(),
                full.film().getName(),
                full.film().getDescription(),
                full.film().getReleaseDate(),
                full.film().getDuration(),
                full.mpa(),
                full.genres() != null ? full.genres() : new ArrayList<>(),
                full.film().getLikes() != null ? full.film().getLikes() : Set.of()
        );
    }

    private FilmResponse toResponse(Film f) {
        return new FilmResponse(
                f.getId(),
                f.getName(),
                f.getDescription(),
                f.getReleaseDate(),
                f.getDuration(),
                f.getMpaId() == null ? null : new Mpa(f.getMpaId(), null, null),
                new ArrayList<>(),
                f.getLikes() != null ? f.getLikes() : Set.of()
        );
    }

    private void validateInput(Integer mpaId, Set<Integer> genreIds) {
        if (mpaId != null && mpaId < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "MPA id must be positive");
        }
        if (genreIds != null && genreIds.stream().anyMatch(id -> id < 1)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Genre ids must be positive");
        }
    }
}
