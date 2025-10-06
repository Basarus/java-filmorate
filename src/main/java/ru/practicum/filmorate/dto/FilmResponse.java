package ru.practicum.filmorate.dto;

import ru.practicum.filmorate.model.Genre;
import ru.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record FilmResponse(
        Integer id,
        String name,
        String description,
        LocalDate releaseDate,
        int duration,
        Mpa mpa,
        List<Genre> genres,
        Set<Integer> likes
) {}
