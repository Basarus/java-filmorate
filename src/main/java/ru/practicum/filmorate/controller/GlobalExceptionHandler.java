package ru.practicum.filmorate.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.filmorate.exception.NotFoundException;
import ru.practicum.filmorate.exception.ValidationException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleRse(ResponseStatusException ex) {
        return new ResponseEntity<>(
                Map.of("error", ex.getReason() != null ? ex.getReason() : ex.getStatusCode().toString()),
                ex.getStatusCode()
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public NotFoundException handleNotFound(NotFoundException ex) {
        return new NotFoundException(ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    public BadRequestException handleValidation(ValidationException ex) {
        return new BadRequestException(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public InternalError handleGeneral(Exception ex) {
        return new InternalError(ex.getMessage());
    }
}
