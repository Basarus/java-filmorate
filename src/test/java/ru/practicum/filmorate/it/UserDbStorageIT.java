package ru.practicum.filmorate.it;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.practicum.filmorate.model.User;
import ru.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ru.yandex.practicum.filmorate.FilmorateApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(UserDbStorage.class)
class UserDbStorageIT {
    @Autowired
    UserDbStorage users;

    @Test
    void createFindUpdateExists() {
        User u = new User();
        u.setEmail("a@b.c");
        u.setLogin("a");
        u.setName("A");
        u.setBirthday(LocalDate.of(2000, 1, 1));
        u = users.save(u);
        assertThat(u.getId()).isNotNull();
        assertThat(users.exists(u.getId())).isTrue();
        var got = users.findById(u.getId()).orElseThrow();
        assertThat(got.getLogin()).isEqualTo("a");
        u.setName("A2");
        users.update(u);
        var got2 = users.findById(u.getId()).orElseThrow();
        assertThat(got2.getName()).isEqualTo("A2");
    }
}
