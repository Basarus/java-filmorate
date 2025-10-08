package ru.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.filmorate.dto.*;
import ru.practicum.filmorate.exception.NotFoundException;
import ru.practicum.filmorate.model.Film;
import ru.practicum.filmorate.model.Mpa;
import ru.practicum.filmorate.service.FilmQueryService;
import ru.practicum.filmorate.service.FilmService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService service;
    private final FilmQueryService query;

    @PostMapping
    public FilmResponse create(@RequestBody @Valid FilmRequestAdapter r) throws BadRequestException {
        validateInput(r.getMpaId(), r.getGenreIds());
        Film f = new Film();
        f.setName(r.getName());
        f.setDescription(r.getDescription());
        f.setReleaseDate(r.getReleaseDate());
        f.setDuration(r.getDuration());
        f.setMpaId(r.getMpaId());
        f.setGenreIds(r.getGenreIds());
        Film created = service.create(f);
        return map(created.getId());
    }

    @PutMapping
    public FilmResponse update(@RequestBody @Valid FilmUpdateRequestAdapter r) throws BadRequestException {
        if (r.getId() == null || r.getId() <= 0) {
            throw new BadRequestException("Film id must be positive");
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
        return map(updated.getId());
    }

    @GetMapping
    public List<FilmResponse> all() {
        return service.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public FilmResponse one(@PathVariable int id) throws BadRequestException {
        if (id <= 0) {
            throw new BadRequestException("Film id must be positive");
        }
        return map(id);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void like(@PathVariable int filmId, @PathVariable int userId) throws BadRequestException {
        if (filmId <= 0 || userId <= 0) {
            throw new BadRequestException("Film and user ids must be positive");
        }
        service.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void unlike(@PathVariable int filmId, @PathVariable int userId) throws BadRequestException {
        if (filmId <= 0 || userId <= 0) {
            throw new BadRequestException("Film and user ids must be positive");
        }
        service.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<FilmResponse> popular(@RequestParam(defaultValue = "10") int count) throws BadRequestException {
        if (count <= 0) {
            throw new BadRequestException("Count must be positive");
        }
        return service.top(count).stream().map(f -> map(f.getId())).collect(Collectors.toList());
    }

    private FilmResponse map(int filmId) {
        var full = query.load(filmId);
        if (full == null) {
            throw new NotFoundException("Film not found");
        }
        return new FilmResponse(full.film().getId(), full.film().getName(), full.film().getDescription(), full.film().getReleaseDate(), full.film().getDuration(), full.mpa(), full.genres() != null ? full.genres() : new ArrayList<>(), full.film().getLikes() != null ? full.film().getLikes() : Set.of());
    }

    private FilmResponse toResponse(Film f) {
        return new FilmResponse(f.getId(), f.getName(), f.getDescription(), f.getReleaseDate(), f.getDuration(), f.getMpaId() == null ? null : new Mpa(f.getMpaId(), null), new ArrayList<>(), f.getLikes() != null ? f.getLikes() : Set.of());
    }

    private void validateInput(Integer mpaId, Set<Integer> genreIds) throws BadRequestException {
        if (mpaId != null && mpaId < 1) {
            throw new BadRequestException("MPA id must be positive");
        }
        if (genreIds != null && genreIds.stream().anyMatch(id -> id < 1)) {
            throw new BadRequestException("Genre ids must be positive");
        }
    }
}
