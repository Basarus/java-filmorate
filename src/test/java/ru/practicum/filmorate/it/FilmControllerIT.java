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
class FilmControllerIT {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;


    @Test
    void createAndPopular() throws Exception {
        String body = om.writeValueAsString(new FilmDto("F", "D", LocalDate.of(2000, 1, 1), 100, 1, Set.of(1, 2)));
        mvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk());
        mvc.perform(get("/films/popular?count=5")).andExpect(status().isOk());
    }


    static class FilmDto {
        public String name;
        public String description;
        public LocalDate releaseDate;
        public int duration;
        public Integer mpaId;
        public Set<Integer> genreIds;

        public FilmDto(String n, String d, LocalDate r, int du, Integer m, Set<Integer> g) {
            name = n;
            description = d;
            releaseDate = r;
            duration = du;
            mpaId = m;
            genreIds = g;
        }
    }
}