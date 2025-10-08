package ru.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.filmorate.exception.NotFoundException;
import ru.practicum.filmorate.model.User;
import ru.practicum.filmorate.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    public User create(@Valid @RequestBody User user) throws BadRequestException {
        if (user == null) {
            throw new BadRequestException("User data must not be null");
        }
        try {
            return service.create(user);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalError("Failed to create user", e);
        }
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) throws BadRequestException {
        if (user == null || user.getId() == null || user.getId() <= 0) {
            throw new BadRequestException("User ID must be positive for update");
        }
        if (!service.exists(user.getId())) {
            throw new NotFoundException("User with id=" + user.getId() + " not found");
        }
        try {
            return service.update(user);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalError("Failed to update user", e);
        }
    }

    @GetMapping
    public List<User> findAll() {
        List<User> users = service.findAll();
        if (users == null || users.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No users found");
        }
        return users;
    }

    @GetMapping("/{id}")
    public Optional<User> findById(@PathVariable Integer id) throws BadRequestException {
        if (id == null || id <= 0) {
            throw new BadRequestException("User ID must be positive");
        }
        return service.findById(id);
    }
}
