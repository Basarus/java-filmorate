package ru.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.filmorate.exception.NotFoundException;
import ru.practicum.filmorate.model.User;
import ru.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class FriendshipController {
    private final UserService service;

    @PutMapping("/{id}/friends/{friendId}")
    public void add(@PathVariable int id, @PathVariable int friendId) throws BadRequestException {
        validateIds(id, friendId);
        if (!service.exists(id)) {
            throw new NotFoundException("User with id=" + id + " not found");
        }
        if (!service.exists(friendId)) {
            throw new NotFoundException("User with id=" + friendId + " not found");
        }
        if (id == friendId) {
            throw new BadRequestException( "Cannot add yourself as a friend");
        }
        service.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void remove(@PathVariable int id, @PathVariable int friendId) throws BadRequestException {
        validateIds(id, friendId);
        if (!service.exists(id)) {
            throw new NotFoundException("User with id=" + id + " not found");
        }
        if (!service.exists(friendId)) {
            throw new NotFoundException("User with id=" + friendId + " not found");
        }
        service.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> friends(@PathVariable int id) throws BadRequestException {
        validateId(id);
        if (!service.exists(id)) {
            throw new NotFoundException("User with id=" + id + " not found");
        }
        return service.friends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> common(@PathVariable int id, @PathVariable int otherId) throws BadRequestException {
        validateIds(id, otherId);
        if (!service.exists(id)) {
            throw new NotFoundException("User with id=" + id + " not found");
        }
        if (!service.exists(otherId)) {
            throw new NotFoundException("User with id=" + otherId + " not found");
        }
        return service.common(id, otherId);
    }

    private void validateId(int id) throws BadRequestException {
        if (id <= 0) {
            throw new BadRequestException("Id must be positive");
        }
    }

    private void validateIds(int id, int friendId) throws BadRequestException {
        if (id <= 0 || friendId <= 0) {
            throw new BadRequestException("Ids must be positive");
        }
    }
}
