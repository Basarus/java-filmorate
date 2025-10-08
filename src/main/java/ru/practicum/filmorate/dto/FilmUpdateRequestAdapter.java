package ru.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

public class FilmUpdateRequestAdapter {
    private final Integer id;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final int duration;
    private final Integer mpaId;
    private final Set<Integer> genreIds;

    @JsonCreator
    public FilmUpdateRequestAdapter(
            @JsonProperty("id") Integer id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("releaseDate") LocalDate releaseDate,
            @JsonProperty("duration") int duration,
            @JsonProperty("mpa") Mpa mpa,
            @JsonProperty("genres") Set<FilmRequestAdapter.GenreDto> genres
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpaId = mpa != null ? mpa.getId() : null;
        this.genreIds = genres != null
                ? genres.stream().map(FilmRequestAdapter.GenreDto::getId).collect(Collectors.toSet())
                : java.util.Collections.emptySet();
    }

    public Integer getId() {
        return id;
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
}
