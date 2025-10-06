package ru.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.filmorate.handler.ErrorHandler;
import ru.practicum.filmorate.model.User;
import ru.practicum.filmorate.service.FilmService;
import ru.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FilmControllerStandaloneTest {

    private MockMvc mvc;
    private InMemoryUserStorage userStorage;

    @BeforeEach
    void setup() {
        var filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        var service = new FilmService(filmStorage, userStorage);
        var controller = new FilmController(service, null);
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new ErrorHandler()).build();
    }

    @Test
    void emptyBodyReturns400() throws Exception {
        mvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());
    }

    @Test
    void negativeDurationReturns400() throws Exception {
        String body = "{" + "\"name\":\"Test\"," + "\"description\":\"D\"," + "\"releaseDate\":\"2000-01-01\"," + "\"duration\":-1," + "\"mpaId\":1," + "\"genreIds\":[]" + "}";
        mvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isBadRequest());
    }

    @Test
    void createThenGetListIs200() throws Exception {
        String body = "{" + "\"name\":\"Ok\"," + "\"description\":\"D\"," + "\"releaseDate\":\"2000-01-01\"," + "\"duration\":90," + "\"mpaId\":1," + "\"genreIds\":[]" + "}";
        mvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk()).andExpect(jsonPath("$.id").exists());

        mvc.perform(get("/films")).andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("Ok"));
    }

    @Test
    void putUnknownIdReturns404() throws Exception {
        String body = "{" + "\"id\":999," + "\"name\":\"X\"," + "\"description\":\"D\"," + "\"releaseDate\":\"2000-01-01\"," + "\"duration\":90," + "\"mpaId\":1," + "\"genreIds\":[]" + "}";
        mvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isNotFound());
    }

    @Test
    void getByIdReturns200() throws Exception {
        String body = "{" + "\"name\":\"Matrix\"," + "\"description\":\"Cyberpunk\"," + "\"releaseDate\":\"1999-03-31\"," + "\"duration\":136," + "\"mpaId\":1," + "\"genreIds\":[]" + "}";
        mvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk());
        mvc.perform(get("/films/1")).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Matrix"));
    }

    @Test
    void getUnknownByIdReturns404() throws Exception {
        mvc.perform(get("/films/999")).andExpect(status().isNotFound());
    }

    @Test
    void likesEmptyOnNewFilm() throws Exception {
        String body = "{" + "\"name\":\"New\"," + "\"description\":\"D\"," + "\"releaseDate\":\"2001-01-01\"," + "\"duration\":120," + "\"mpaId\":1," + "\"genreIds\":[]" + "}";
        mvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk());
        mvc.perform(get("/films/1")).andExpect(status().isOk()).andExpect(jsonPath("$.likes.length()", is(0)));
    }

    @Test
    void likeFlowAndPopular() throws Exception {
        User u = new User();
        u.setEmail("u@ex.com");
        u.setLogin("neo");
        u.setName("");
        u.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.save(u);

        String f1 = "{" + "\"name\":\"FilmA\"," + "\"description\":\"D\"," + "\"releaseDate\":\"2001-01-01\"," + "\"duration\":100," + "\"mpaId\":1," + "\"genreIds\":[]" + "}";
        String f2 = "{" + "\"name\":\"FilmB\"," + "\"description\":\"D\"," + "\"releaseDate\":\"2002-02-02\"," + "\"duration\":110," + "\"mpaId\":1," + "\"genreIds\":[]" + "}";

        mvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(f1)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
        mvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(f2)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(2));

        mvc.perform(put("/films/2/like/1")).andExpect(status().isOk());

        mvc.perform(get("/films/popular?count=1")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(2));
    }

    @Test
    void likeIsIdempotent() throws Exception {
        User u = new User();
        u.setEmail("z@ex.com");
        u.setLogin("z");
        u.setName("");
        u.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.save(u);

        String f = "{" + "\"name\":\"F\"," + "\"description\":\"D\"," + "\"releaseDate\":\"2001-01-01\"," + "\"duration\":100," + "\"mpaId\":1," + "\"genreIds\":[]" + "}";
        mvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(f)).andExpect(status().isOk());

        mvc.perform(put("/films/1/like/1")).andExpect(status().isOk());
        mvc.perform(put("/films/1/like/1")).andExpect(status().isOk());

        mvc.perform(get("/films/1")).andExpect(status().isOk()).andExpect(jsonPath("$.likes.length()", is(1)));
    }

    @Test
    void unlikeNonExistingOk() throws Exception {
        var user = new ru.practicum.filmorate.model.User();
        user.setEmail("u@ex.com");
        user.setLogin("u");
        user.setName("");
        user.setBirthday(java.time.LocalDate.of(1990, 1, 1));
        userStorage.save(user);

        String f = "{" + "\"name\":\"F2\"," + "\"description\":\"D\"," + "\"duration\":100," + "\"mpaId\":1," + "\"releaseDate\":\"2001-01-01\"," + "\"duration\":100" + "}";
        mvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(f)).andExpect(status().isOk());

        mvc.perform(delete("/films/1/like/" + user.getId())).andExpect(status().isOk());

        mvc.perform(get("/films/1")).andExpect(status().isOk()).andExpect(jsonPath("$.likes.length()", org.hamcrest.Matchers.is(0)));
    }

    @Test
    void likeUnknownUserReturns404() throws Exception {
        String f = "{" + "\"name\":\"F3\"," + "\"description\":\"D\"," + "\"duration\":100," + "\"mpaId\":1," + "\"releaseDate\":\"2001-01-01\"," + "\"duration\":100" + "}";
        mvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(f)).andExpect(status().isOk());
        mvc.perform(put("/films/1/like/999")).andExpect(status().isNotFound());
    }

    @Test
    void likeUnknownFilmReturns404() throws Exception {
        User u = new User();
        u.setEmail("a@ex.com");
        u.setLogin("a");
        u.setName("");
        u.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.save(u);
        mvc.perform(put("/films/999/like/1")).andExpect(status().isNotFound());
    }

    @Test
    void popularDefaultCountIs10() throws Exception {
        User u = new User();
        u.setEmail("p@ex.com");
        u.setLogin("p");
        u.setName("");
        u.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.save(u);

        IntStream.rangeClosed(1, 12).forEach(i -> {
            String f = "{" + "\"name\":\"F" + i + "\"," + "\"description\":\"D\"," + "\"duration\":100," + "\"duration\":100," + "\"mpaId\":1," + "\"releaseDate\":\"2001-01-01\"," + "\"duration\":100" + "}";
            try {
                mvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(f)).andExpect(status().isOk());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        mvc.perform(put("/films/12/like/1")).andExpect(status().isOk());
        mvc.perform(put("/films/11/like/1")).andExpect(status().isOk());

        mvc.perform(get("/films/popular")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(10)));
    }

    @Test
    void popularCountMoreThanExistingReturnsAll() throws Exception {
        String f = "{" + "\"name\":\"OnlyOne\"," + "\"description\":\"D\"," + "\"duration\":100," + "\"mpaId\":1," + "\"releaseDate\":\"2001-01-01\"," + "\"duration\":100" + "}";
        mvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(f)).andExpect(status().isOk());

        mvc.perform(get("/films/popular?count=100")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void popularTieBreakById() throws Exception {
        User u = new User();
        u.setEmail("t@ex.com");
        u.setLogin("t");
        u.setName("");
        u.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.save(u);

        String f1 = "{" + "\"name\":\"A\"," + "\"description\":\"D\"," + "\"duration\":100," + "\"mpaId\":1," + "\"releaseDate\":\"2001-01-01\"," + "\"duration\":100" + "}";
        String f2 = "{" + "\"name\":\"B\"," + "\"description\":\"D\"," + "\"duration\":100," + "\"mpaId\":1," + "\"releaseDate\":\"2001-01-01\"," + "\"duration\":100" + "}";
        mvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(f1)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
        mvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(f2)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(2));

        mvc.perform(put("/films/1/like/1")).andExpect(status().isOk());
        mvc.perform(put("/films/2/like/1")).andExpect(status().isOk());

        mvc.perform(get("/films/popular?count=2")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(1)).andExpect(jsonPath("$[1].id").value(2));
    }
}
