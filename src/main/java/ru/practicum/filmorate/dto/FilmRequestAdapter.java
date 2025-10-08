package ru.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import ru.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

public class FilmRequestAdapter {

    @NotBlank
    private final String name;

    @Size(max = 200)
    private final String description;

    @NotNull
    private final LocalDate releaseDate;

    @Positive
    private final int duration;

    @NotNull
    private final Integer mpaId;

    private final Set<Integer> genreIds;

    @JsonCreator
    public FilmRequestAdapter(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("releaseDate") LocalDate releaseDate,
            @JsonProperty("duration") int duration,
            @JsonProperty("mpa") Mpa mpa,
            @JsonProperty("genres") Set<GenreDto> genres
    ) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpaId = mpa != null ? mpa.getId() : null;
        this.genreIds = genres != null
                ? genres.stream().map(GenreDto::getId).collect(Collectors.toSet())
                : java.util.Collections.emptySet();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public int getDuration() {
        return duration;
    }

    public Integer getMpaId() {
        return mpaId;
    }

    public Set<Integer> getGenreIds() {
        return genreIds;
    }

    public static class GenreDto {
        private final Integer id;

        @JsonCreator
        public GenreDto(@JsonProperty("id") Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }
    }
}
