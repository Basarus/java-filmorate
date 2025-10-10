package ru.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.filmorate.handler.GlobalExceptionHandler;
import ru.practicum.filmorate.service.UserService;
import ru.practicum.filmorate.storage.friendship.InMemoryFriendshipStorage;
import ru.practicum.filmorate.storage.user.InMemoryUserStorage;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ru.yandex.practicum.filmorate.FilmorateApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerStandaloneTest {

    private MockMvc mvc;

    @BeforeEach
    void setup() {
        var userStorage = new InMemoryUserStorage();
        var friendStorage = new InMemoryFriendshipStorage();
        var service = new UserService(userStorage, friendStorage);
        var controller = new UserController(service);
        var friendshipController = new FriendshipController(service);
        mvc = MockMvcBuilders.standaloneSetup(controller, friendshipController).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    void loginWithSpacesReturns400() throws Exception {
        String body = "{" + "\"email\":\"a@b.com\"," + "\"login\":\"bad login\"," + "\"name\":\"\"," + "\"birthday\":\"2000-01-01\"" + "}";
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isBadRequest());
    }

    @Test
    void emptyNameFallbackToLogin() throws Exception {
        String body = "{" + "\"email\":\"user@example.com\"," + "\"login\":\"neo\"," + "\"name\":\"\"," + "\"birthday\":\"2000-01-01\"" + "}";
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("neo"));
    }

    @Test
    void putUnknownIdReturns404() throws Exception {
        String body = "{" + "\"id\":777," + "\"email\":\"user@example.com\"," + "\"login\":\"neo\"," + "\"name\":\"N\"," + "\"birthday\":\"2000-01-01\"" + "}";
        mvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isNotFound());
    }

    @Test
    void createThenGetListIs200() throws Exception {
        String body = "{" + "\"email\":\"ok@example.com\"," + "\"login\":\"trinity\"," + "\"name\":\"\"," + "\"birthday\":\"1990-01-01\"" + "}";
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk()).andExpect(jsonPath("$.id").exists());

        mvc.perform(get("/users")).andExpect(status().isOk()).andExpect(jsonPath("$[0].login").value("trinity"));
    }

    @Test
    void getByIdReturns200() throws Exception {
        String body = "{" + "\"email\":\"get@example.com\"," + "\"login\":\"smith\"," + "\"name\":\"\"," + "\"birthday\":\"1980-01-01\"" + "}";
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk());
        mvc.perform(get("/users/1")).andExpect(status().isOk()).andExpect(jsonPath("$.login").value("smith"));
    }

    @Test
    void getUnknownUserByIdReturns404() throws Exception {
        mvc.perform(get("/users/999")).andExpect(status().isNotFound());
    }

    @Test
    void friendsListInitiallyEmpty() throws Exception {
        String body = "{" + "\"email\":\"a@b.com\"," + "\"login\":\"u1\"," + "\"name\":\"\"," + "\"birthday\":\"1990-01-01\"" + "}";
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk());
        mvc.perform(get("/users/1/friends")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void addAndRemoveFriendsFlow() throws Exception {
        String u1 = "{" + "\"email\":\"a@b.com\"," + "\"login\":\"u1\"," + "\"name\":\"\"," + "\"birthday\":\"1990-01-01\"" + "}";
        String u2 = "{" + "\"email\":\"b@b.com\"," + "\"login\":\"u2\"," + "\"name\":\"\"," + "\"birthday\":\"1991-01-01\"" + "}";
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(u1)).andExpect(status().isOk());
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(u2)).andExpect(status().isOk());

        mvc.perform(put("/users/1/friends/2")).andExpect(status().isOk());
        mvc.perform(get("/users/1/friends")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(2));

        mvc.perform(delete("/users/1/friends/2")).andExpect(status().isOk());
        mvc.perform(get("/users/1/friends")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void addFriendIsIdempotent() throws Exception {
        String u1 = "{" + "\"email\":\"a@b.com\"," + "\"login\":\"u1\"," + "\"name\":\"\"," + "\"birthday\":\"1990-01-01\"" + "}";
        String u2 = "{" + "\"email\":\"b@b.com\"," + "\"login\":\"u2\"," + "\"name\":\"\"," + "\"birthday\":\"1991-01-01\"" + "}";
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(u1)).andExpect(status().isOk());
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(u2)).andExpect(status().isOk());

        mvc.perform(put("/users/1/friends/2")).andExpect(status().isOk());
        mvc.perform(put("/users/1/friends/2")).andExpect(status().isOk());

        mvc.perform(get("/users/1/friends")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void deleteNotFriendsOk() throws Exception {
        String u1 = "{" + "\"email\":\"a@b.com\"," + "\"login\":\"u1\"," + "\"name\":\"\"," + "\"birthday\":\"1990-01-01\"" + "}";
        String u2 = "{" + "\"email\":\"b@b.com\"," + "\"login\":\"u2\"," + "\"name\":\"\"," + "\"birthday\":\"1991-01-01\"" + "}";
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(u1)).andExpect(status().isOk());
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(u2)).andExpect(status().isOk());

        mvc.perform(delete("/users/1/friends/2")).andExpect(status().isOk());
        mvc.perform(get("/users/1/friends")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void addSelfFriendReturns400() throws Exception {
        String u1 = "{" + "\"email\":\"a@b.com\"," + "\"login\":\"u1\"," + "\"name\":\"\"," + "\"birthday\":\"1990-01-01\"" + "}";
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(u1)).andExpect(status().isOk());
        mvc.perform(put("/users/1/friends/1")).andExpect(status().isBadRequest());
    }

    @Test
    void addFriendUnknownUserReturns404_byActor() throws Exception {
        String u2 = "{" + "\"email\":\"b@b.com\"," + "\"login\":\"u2\"," + "\"name\":\"\"," + "\"birthday\":\"1991-01-01\"" + "}";
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(u2)).andExpect(status().isOk());
        mvc.perform(put("/users/999/friends/1")).andExpect(status().isNotFound());
    }

    @Test
    void addFriendUnknownUserReturns404_byFriend() throws Exception {
        String u1 = "{" + "\"email\":\"a@b.com\"," + "\"login\":\"u1\"," + "\"name\":\"\"," + "\"birthday\":\"1990-01-01\"" + "}";
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(u1)).andExpect(status().isOk());
        mvc.perform(put("/users/1/friends/999")).andExpect(status().isNotFound());
    }

    @Test
    void commonFriendsReturnsCorrect() throws Exception {
        String u1 = "{" + "\"email\":\"a@b.com\"," + "\"login\":\"u1\"," + "\"name\":\"\"," + "\"birthday\":\"1990-01-01\"" + "}";
        String u2 = "{" + "\"email\":\"b@b.com\"," + "\"login\":\"u2\"," + "\"name\":\"\"," + "\"birthday\":\"1991-01-01\"" + "}";
        String u3 = "{" + "\"email\":\"c@b.com\"," + "\"login\":\"u3\"," + "\"name\":\"\"," + "\"birthday\":\"1992-01-01\"" + "}";

        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(u1)).andExpect(status().isOk());
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(u2)).andExpect(status().isOk());
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(u3)).andExpect(status().isOk());

        mvc.perform(put("/users/1/friends/3")).andExpect(status().isOk());
        mvc.perform(put("/users/2/friends/3")).andExpect(status().isOk());

        mvc.perform(get("/users/1/friends/common/2")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(3));
    }
}
