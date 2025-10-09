package ru.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.filmorate.model.Mpa;
import ru.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final MpaStorage mpaStorage;

    @GetMapping
    public List<Mpa> getAllMpa() {
        try {
            List<Mpa> mpaList = mpaStorage.findAll();
            if (mpaList == null || mpaList.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No MPA ratings found");
            }
            return mpaList;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to load MPA ratings", e);
        }
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable int id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "MPA ID must be positive");
        }

        try {
            return mpaStorage.findById(id)
                    .orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.NOT_FOUND, "MPA with id=" + id + " not found"));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to load MPA rating by id=" + id, e);
        }
    }
}
