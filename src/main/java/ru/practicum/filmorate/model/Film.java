package ru.practicum.filmorate.model;

import lombok.Data;
import ru.practicum.filmorate.storage.Identifiable;
import jakarta.validation.constraints.*;
import ru.practicum.filmorate.validation.DateNotBefore;

import java.time.LocalDate;

@Data
public class Film implements Identifiable {
    private Integer id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @NotNull
    @DateNotBefore("1895-12-28")
    private LocalDate releaseDate;

    @Positive
    private int duration;
}
