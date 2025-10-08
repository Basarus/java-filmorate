package ru.practicum.filmorate.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.filmorate.exception.NotFoundException;
import ru.practicum.filmorate.exception.ValidationException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BadRequestException handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> body = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> body.put(e.getField(), e.getDefaultMessage()));
        return new BadRequestException();
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
