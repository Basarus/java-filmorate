package ru.practicum.filmorate;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.practicum.filmorate.storage.film.DbFilmStorage;
import ru.practicum.filmorate.storage.film.FilmStorage;
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

}

