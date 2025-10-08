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
        return mpaStorage.findAll();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable int id) {
        return mpaStorage.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "MPA not found"));
    }
}
