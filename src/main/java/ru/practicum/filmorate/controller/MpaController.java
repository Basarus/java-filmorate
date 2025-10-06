package ru.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.filmorate.dao.MpaDao;
import ru.practicum.filmorate.model.Mpa;


import java.util.List;


@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaDao dao;

    @GetMapping
    public List<Mpa> all() {
        return dao.findAll();
    }

    @GetMapping("/{id}")
    public Mpa one(@PathVariable int id) {
        return dao.findById(id).orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));
    }
}