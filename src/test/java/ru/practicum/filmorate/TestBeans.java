package ru.practicum.filmorate;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import ru.practicum.filmorate.storage.film.FilmStorage;
import ru.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.practicum.filmorate.storage.friendship.InMemoryFriendshipStorage;
import ru.practicum.filmorate.storage.genre.GenreStorage;
import ru.practicum.filmorate.storage.genre.InMemoryGenreStorage;
import ru.practicum.filmorate.storage.likes.InMemoryLikesStorage;
import ru.practicum.filmorate.storage.likes.LikesStorage;
import ru.practicum.filmorate.storage.mpa.InMemoryMpaStorage;
import ru.practicum.filmorate.storage.mpa.MpaStorage;
import ru.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.practicum.filmorate.storage.user.UserStorage;

@TestConfiguration
@Profile("test")
public class TestBeans {

    @Bean
    @Primary
    public FilmStorage filmStorage() {
        return new InMemoryFilmStorage();
    }

    @Bean
    @Primary
    public UserStorage userStorage() {
        return new InMemoryUserStorage();
    }

    @Bean
    @Primary
    public FriendshipStorage friendshipDao() {
        return new InMemoryFriendshipStorage();
    }

    @Bean
    @Primary
    public LikesStorage likesStorage() {
        return new InMemoryLikesStorage();
    }

    @Bean
    @Primary
    public MpaStorage mpaStorage() {
        return new InMemoryMpaStorage();
    }

    @Bean
    @Primary
    public GenreStorage genreStorage() {
        return new InMemoryGenreStorage();
    }
}

