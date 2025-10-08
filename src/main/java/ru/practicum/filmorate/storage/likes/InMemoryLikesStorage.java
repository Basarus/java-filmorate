package ru.practicum.filmorate.storage.likes;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Profile("test")
public class InMemoryLikesStorage implements LikesStorage {
    private final Map<Integer, Set<Integer>> likes = new HashMap<>();

    @Override
    public void like(int filmId, int userId) {
        likes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void unlike(int filmId, int userId) {
        likes.getOrDefault(filmId, Collections.emptySet()).remove(userId);
    }

    public Set<Integer> getLikes(int filmId) {
        return likes.getOrDefault(filmId, Collections.emptySet());
    }
}
