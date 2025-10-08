package ru.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService service;
    private final FilmQueryService query;

    @PostMapping
    public FilmResponse create(@RequestBody @Valid FilmRequestAdapter r) {
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
    public FilmResponse update(@RequestBody @Valid FilmUpdateRequestAdapter r) {
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
    public FilmResponse one(@PathVariable int id) {
        return map(id);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void like(@PathVariable int filmId, @PathVariable int userId) {
        service.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void unlike(@PathVariable int filmId, @PathVariable int userId) {
        service.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<FilmResponse> popular(@RequestParam(defaultValue = "10") int count) {
        return service.top(count).stream().map(f -> map(f.getId())).collect(Collectors.toList());
    }

    private FilmResponse map(int filmId) {
        var full = query.load(filmId);
        if (full == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found after creation");
        }

        return new FilmResponse(full.film().getId(), full.film().getName(), full.film().getDescription(), full.film().getReleaseDate(), full.film().getDuration(), full.mpa(), full.genres(), full.film().getLikes());
    }

    private FilmResponse toResponse(Film f) {
        return new FilmResponse(f.getId(), f.getName(), f.getDescription(), f.getReleaseDate(), f.getDuration(), f.getMpaId() == null ? null : new Mpa(f.getMpaId(), null), new ArrayList<>(), f.getLikes());
    }
}
