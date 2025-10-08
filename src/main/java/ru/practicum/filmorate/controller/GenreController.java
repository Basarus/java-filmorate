package ru.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.filmorate.model.Genre;
import ru.practicum.filmorate.storage.genre.GenreStorage;


import java.util.List;


@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreStorage dao;

    @GetMapping
    public List<Genre> all() {
        return dao.findAll();
    }

    @GetMapping("/{id}")
    public Genre one(@PathVariable int id) {
        return dao.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}