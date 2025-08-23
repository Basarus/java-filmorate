package ru.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.filmorate.handler.ErrorHandler;
import ru.practicum.filmorate.service.FilmService;
import ru.practicum.filmorate.storage.InMemoryFilmStorage;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FilmControllerStandaloneTest {

    private MockMvc mvc;

    @BeforeEach
    void setup() {
        var storage = new InMemoryFilmStorage();
        var service = new FilmService(storage);
        var controller = new FilmController(service);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @Test
    void emptyBodyReturns400() throws Exception {
        mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void negativeDurationReturns400() throws Exception {
        String body = """
        {
          "name": "Test",
          "description": "D",
          "releaseDate": "2000-01-01",
          "duration": -1
        }""";
        mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createThenGetListIs200() throws Exception {
        String body = """
        {
          "name": "Ok",
          "description": "D",
          "releaseDate": "2000-01-01",
          "duration": 90
        }""";
        mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        mvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Ok"));
    }

    @Test
    void putUnknownIdReturns404() throws Exception {
        String body = """
        {
          "id": 999,
          "name": "X",
          "description": "D",
          "releaseDate": "2000-01-01",
          "duration": 90
        }""";
        mvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }
}
