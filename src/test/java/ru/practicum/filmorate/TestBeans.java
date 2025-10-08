package ru.practicum.filmorate;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.practicum.filmorate.service.PopularityService;
import ru.practicum.filmorate.storage.film.FilmStorage;
import ru.practicum.filmorate.storage.film.DbFilmStorage;
import ru.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.practicum.filmorate.storage.friendship.InMemoryFriendshipStorage;
import ru.practicum.filmorate.storage.genre.GenreStorage;
import ru.practicum.filmorate.storage.genre.InMemoryGenreStorage;
import ru.practicum.filmorate.storage.likes.InMemoryLikesStorage;
import ru.practicum.filmorate.storage.likes.LikesStorage;
import ru.practicum.filmorate.storage.mpa.InMemoryMpaStorage;
import ru.practicum.filmorate.storage.mpa.MpaStorage;
import ru.practicum.filmorate.storage.user.UserDbStorage;
import ru.practicum.filmorate.storage.user.UserStorage;

@TestConfiguration
@Profile("test")
public class TestBeans {

    @Bean
    @Primary
    public FilmStorage filmStorage(JdbcTemplate jdbcTemplate) {
        return new DbFilmStorage(jdbcTemplate);
    }

    @Bean
    @Primary
    public UserStorage userStorage(JdbcTemplate jdbcTemplate) {
        return new UserDbStorage(jdbcTemplate);
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

    @Bean
    @Primary
    public PopularityService popularityService() {
        return new PopularityService(null);
    }
}

