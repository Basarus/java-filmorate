package ru.practicum.filmorate.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.practicum.filmorate.model.Film;
import ru.practicum.filmorate.model.Genre;
import ru.practicum.filmorate.model.Mpa;
import ru.practicum.filmorate.storage.film.FilmStorage;
import ru.practicum.filmorate.storage.genre.GenreStorage;
import ru.practicum.filmorate.storage.likes.LikesStorage;
import ru.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Profile("test")
public class InMemoryFilmQueryService extends FilmQueryService {

    private final FilmStorage films;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final LikesStorage likesStorage;

    public InMemoryFilmQueryService(FilmStorage films, MpaStorage mpaStorage, GenreStorage genreStorage, LikesStorage likesStorage) {
        super(null);
        this.films = films;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.likesStorage = likesStorage;
    }

    @Override
    public FullFilm load(int filmId) {

        Film film = films.findById(filmId).orElse(null);

        if (film == null) return null;

        Mpa mpa = null;
        if (film.getMpaId() != null) {
            mpa = mpaStorage.findById(film.getMpaId()).orElse(null);
        }

        List<Genre> genres = new ArrayList<>();
        if (film.getGenreIds() != null && !film.getGenreIds().isEmpty()) {
            genres = film.getGenreIds().stream().map(genreStorage::findById).flatMap(Optional::stream).toList();
        }

        film.getLikes().clear();
        film.getLikes().addAll(likesStorage.getLikes(filmId));

        return new FullFilm(film, mpa, genres);
    }
}
