package ru.practicum.filmorate.it;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.practicum.filmorate.TestBeans;
import ru.practicum.filmorate.service.PopularityService;
import ru.yandex.practicum.filmorate.FilmorateApplication;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@SpringBootTest(classes = {FilmorateApplication.class, TestBeans.class})
@Import({PopularityService.class})
class PopularityServiceIT {
    @Autowired
    PopularityService service;

    @Test
    void top() {
        service.top(10);
    }
}