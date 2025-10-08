package ru.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.filmorate.exception.NotFoundException;
import ru.practicum.filmorate.model.Genre;
import ru.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreStorage genreStorage;

    @GetMapping
    public List<Genre> getAllGenres() {
        try {
            List<Genre> genres = genreStorage.findAll();
            if (genres == null || genres.isEmpty()) {
                throw new NotFoundException("No genres found");
            }
            return genres;
        } catch (Exception e) {
            throw new InternalError("Failed to load genres", e);
        }
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id) {
        if (id <= 0) {
            throw new NotFoundException("Genre ID must be positive");
        }

        try {
            return genreStorage.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre with id=" + id + " not found"));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalError("Failed to load genre by id=" + id, e);
        }
    }
}
