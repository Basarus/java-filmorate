package ru.practicum.filmorate.model;

import lombok.Data;
import ru.practicum.filmorate.storage.Identifiable;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User implements Identifiable {
    private Integer id;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "\\S+")
    private String login;

    private String name;

    @NotNull
    @PastOrPresent
    private LocalDate birthday;

    private final Set<Integer> friends = new HashSet<>();
}
