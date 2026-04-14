package ru.practicum.filmorate.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ru.yandex.practicum.filmorate.FilmorateApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FilmUpdateIT {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @Test
    void updateWithIdInBody() throws Exception {
        var create = new FilmCreate("N", "D", LocalDate.of(2000, 1, 1), 100, 1, Set.of(1));
        var res = mvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(create))).andExpect(status().isOk()).andReturn();
        int id = com.jayway.jsonpath.JsonPath.read(res.getResponse().getContentAsString(), "$.id");
        var upd = new FilmUpdate(id, "N2", "D2", LocalDate.of(2001, 1, 1), 120, 1, Set.of(2));
        mvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(upd))).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("N2"));
    }

    record FilmCreate(String name, String description, LocalDate releaseDate, int duration, Integer mpaId,
                      Set<Integer> genreIds) {
    }

    record FilmUpdate(Integer id, String name, String description, LocalDate releaseDate, int duration, Integer mpaId,
                      Set<Integer> genreIds) {
    }
}
