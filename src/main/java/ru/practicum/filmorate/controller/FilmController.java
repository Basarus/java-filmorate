package ru.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.filmorate.model.Film;
import ru.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService service;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return service.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return service.update(film);
    }

    @GetMapping
    public List<Film> findAll() {
        return service.findAll();
    }
}
