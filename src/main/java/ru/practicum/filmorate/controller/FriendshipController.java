package ru.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.filmorate.model.User;
import ru.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class FriendshipController {
    private final UserService service;

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void add(@PathVariable int id, @PathVariable int friendId) throws BadRequestException {
        log.info("User {} added user {} as a friend", id, friendId);
        validateIds(id, friendId);
        if (!service.exists(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id=" + id + " not found");
        }
        if (!service.exists(friendId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id=" + friendId + " not found");
        }
        if (id == friendId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot add yourself as a friend");
        }
        service.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void remove(@PathVariable int id, @PathVariable int friendId) throws BadRequestException {
        log.info("User {} removed user {} from friends", id, friendId);
        validateIds(id, friendId);
        if (!service.exists(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id=" + id + " not found");
        }
        if (!service.exists(friendId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id=" + friendId + " not found");
        }
        service.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> friends(@PathVariable int id) throws BadRequestException {
        validateId(id);
        if (!service.exists(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id=" + id + " not found");
        }
        return service.friends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> common(@PathVariable int id, @PathVariable int otherId) throws BadRequestException {
        validateIds(id, otherId);
        if (!service.exists(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id=" + id + " not found");
        }
        if (!service.exists(otherId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id=" + otherId + " not found");
        }
        return service.common(id, otherId);
    }

    private void validateId(int id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id must be positive");
        }
    }

    private void validateIds(int id, int friendId) {
        if (id <= 0 || friendId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ids must be positive");
        }
    }
}
