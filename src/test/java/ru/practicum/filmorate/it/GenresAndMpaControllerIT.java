package ru.practicum.filmorate.it;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.filmorate.TestBeans;
import ru.yandex.practicum.filmorate.FilmorateApplication;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = {FilmorateApplication.class, TestBeans.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GenresAndMpaControllerIT {
    @Autowired
    MockMvc mvc;

    @Test
    void genresAll() throws Exception {
        mvc.perform(get("/genres")).andExpect(status().isOk());
    }

    @Test
    void mpaAll() throws Exception {
        mvc.perform(get("/mpa")).andExpect(status().isOk());
    }
}