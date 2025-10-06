package ru.practicum.filmorate.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Set;

public record FilmRequest(@NotBlank String name, @Size(max = 200) String description, @NotNull LocalDate releaseDate,
                          @Positive int duration, @NotNull Integer mpaId, Set<Integer> genreIds) {
}
