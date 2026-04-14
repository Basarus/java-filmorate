package ru.practicum.filmorate.controller;

import jakarta.validation.Valid;
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
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User data must not be null");
        }
        log.info("Creating user: email={}, login={}, name={}", user.getEmail(), user.getLogin(), user.getName());
        User created = service.create(user);
        log.info("User created successfully with id={}", created.getId());
        return created;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) throws BadRequestException {
        if (user == null || user.getId() == null || user.getId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID must be positive for update");
        }
        log.info("Updating user with id={}", user.getId());
        if (!service.exists(user.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id=" + user.getId() + " not found");
        }
        User updated = service.update(user);
        log.info("User updated successfully: id={}, email={}, login={}", updated.getId(), updated.getEmail(), updated.getLogin());
        return updated;
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
    public User findById(@PathVariable Integer id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID must be positive");
        }
        return service.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id=" + id + " not found"));
    }
}
