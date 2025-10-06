package ru.practicum.filmorate.it;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ru.yandex.practicum.filmorate.FilmorateApplication.class)
@AutoConfigureMockMvc
class DictControllersIT {
    @Autowired
    MockMvc mvc;

    @Test
    void genreByIdOk() throws Exception {
        mvc.perform(get("/genres/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").isNotEmpty());
    }

    @Test
    void genreByIdNotFound() throws Exception {
        mvc.perform(get("/genres/9999")).andExpect(status().isNotFound());
    }

    @Test
    void mpaByIdOk() throws Exception {
        mvc.perform(get("/mpa/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").isNotEmpty());
    }

    @Test
    void mpaByIdNotFound() throws Exception {
        mvc.perform(get("/mpa/9999")).andExpect(status().isNotFound());
    }
}
