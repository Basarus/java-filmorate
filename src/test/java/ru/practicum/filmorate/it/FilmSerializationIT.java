package ru.practicum.filmorate.it;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.filmorate.TestBeans;
import ru.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ru.yandex.practicum.filmorate.FilmorateApplication.class)
@AutoConfigureMockMvc
@Import(TestBeans.class)
@ActiveProfiles("test")
class FilmSerializationIT {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @Test
    void createGetReturnsExpandedMpaAndGenres() throws Exception {
        var req = new FilmDto(
                "Test film",
                "Some description",
                LocalDate.of(2000, 1, 1),
                90,
                new Mpa(1, null, null),
                List.of(new GenreDto(1), new GenreDto(2))
        );

        var create = mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode created = om.readTree(create.getResponse().getContentAsString());
        assertThat(created.get("mpa").get("id").asInt()).isEqualTo(1);
        assertThat(created.get("genres").isArray()).isTrue();

        int filmId = created.get("id").asInt();

        var get = mvc.perform(get("/films/" + filmId))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loaded = om.readTree(get.getResponse().getContentAsString());
        assertThat(loaded.get("mpa").get("name").asText()).isNotEmpty();
        assertThat(loaded.get("genres").size()).isGreaterThanOrEqualTo(1);
    }

    record GenreDto(int id) {}

    record FilmDto(
            String name,
            String description,
            LocalDate releaseDate,
            int duration,
            Mpa mpa,
            List<GenreDto> genres
    ) {}
}
