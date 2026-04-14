package ru.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import ru.practicum.filmorate.model.Mpa;
import ru.practicum.filmorate.validation.DateNotBefore;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class FilmCreateRequest {

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @NotNull
    @DateNotBefore("1895-12-28")
    private LocalDate releaseDate;

    @Positive
    private int duration;

    private Integer mpaId;

    private Set<Integer> genreIds = new HashSet<>();

    public FilmCreateRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Integer getMpaId() {
        return mpaId;
    }

    public void setMpa(Mpa mpa) {
        this.mpaId = mpa != null ? mpa.getId() : null;
    }

    public Set<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenres(Set<GenreDto> genres) {
        this.genreIds = genres != null
                ? genres.stream().map(GenreDto::getId).collect(Collectors.toSet())
                : Collections.emptySet();
    }

    public static class GenreDto {
        private Integer id;

        public GenreDto() {
        }

        public GenreDto(Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }
}
