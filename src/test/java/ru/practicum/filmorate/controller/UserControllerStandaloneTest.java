package ru.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.filmorate.handler.ErrorHandler;
import ru.practicum.filmorate.service.UserService;
import ru.practicum.filmorate.storage.InMemoryUserStorage;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerStandaloneTest {

    private MockMvc mvc;

    @BeforeEach
    void setup() {
        var storage = new InMemoryUserStorage();
        var service = new UserService(storage);
        var controller = new UserController(service);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @Test
    void loginWithSpacesReturns400() throws Exception {
        String body = """
        {
          "email": "a@b.com",
          "login": "bad login",
          "name": "",
          "birthday": "2000-01-01"
        }""";
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void emptyNameFallbackToLogin() throws Exception {
        String body = """
        {
          "email": "user@example.com",
          "login": "neo",
          "name": "",
          "birthday": "2000-01-01"
        }""";
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("neo"));
    }

    @Test
    void putUnknownIdReturns404() throws Exception {
        String body = """
        {
          "id": 777,
          "email": "user@example.com",
          "login": "neo",
          "name": "N",
          "birthday": "2000-01-01"
        }""";
        mvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void createThenGetListIs200() throws Exception {
        String body = """
        {
          "email": "ok@example.com",
          "login": "trinity",
          "name": "",
          "birthday": "1990-01-01"
        }""";
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].login").value("trinity"));
    }
}
