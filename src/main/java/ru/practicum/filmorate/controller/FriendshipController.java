package ru.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.filmorate.model.User;
import ru.practicum.filmorate.service.UserService;


import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class FriendshipController {
    private final UserService service;

    @PutMapping("/{id}/friends/{friendId}")
    public void add(@PathVariable int id, @PathVariable int friendId) {
        service.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void remove(@PathVariable int id, @PathVariable int friendId) {
        service.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> friends(@PathVariable int id) {
        return service.friends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> common(@PathVariable int id, @PathVariable int otherId) {
        return service.common(id, otherId);
    }
}