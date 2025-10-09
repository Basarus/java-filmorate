package ru.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.filmorate.model.User;
import ru.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User data must not be null");
        }
        try {
            return service.create(user);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create user", e);
        }
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) throws BadRequestException {
        if (user == null || user.getId() == null || user.getId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID must be positive for update");
        }
        if (!service.exists(user.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id=" + user.getId() + " not found");
        }
        try {
            return service.update(user);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update user", e);
        }
    }

    @GetMapping
    public List<User> findAll() {
        try {
            List<User> users = service.findAll();
            if (users == null || users.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No users found");
            }
            return users;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to load users", e);
        }
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Integer id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID must be positive");
        }
        try {
            return service.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id=" + id + " not found"));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to load user by id=" + id, e);
        }
    }
}
