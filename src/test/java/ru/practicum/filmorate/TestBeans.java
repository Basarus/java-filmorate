package ru.practicum.filmorate;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.practicum.filmorate.dao.*;
import ru.practicum.filmorate.service.PopularityService;
import ru.practicum.filmorate.storage.film.FilmStorage;
import ru.practicum.filmorate.storage.film.DbFilmStorage;
import ru.practicum.filmorate.storage.genre.DbGenreDao;
import ru.practicum.filmorate.storage.mpa.DbMpaDao;
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
    public FriendshipDao friendshipDao() {
        return new InMemoryFriendshipDao();
    }

    @Bean
    @Primary
    public LikesDao likesDao() {
        return new InMemoryLikesDao();
    }

    @Bean
    @Primary
    public MpaDao mpaDao(JdbcTemplate jdbcTemplate) {
        return new DbMpaDao(jdbcTemplate);
    }

    @Bean
    @Primary
    public GenreDao genreDao() {
        return new InMemoryGenreDao();
    }

    @Bean
    @Primary
    public PopularityService popularityService() {
        return new PopularityService(null);
    }
}

