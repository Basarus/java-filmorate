package ru.practicum.filmorate.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Primary
@Profile("!test")
public class InMemoryLikesDao extends LikesDao {
    private final Map<Integer, Set<Integer>> likes = new HashMap<>();

    public InMemoryLikesDao() {
        super(null);
    }

    @Override
    public void like(int filmId, int userId) {
        likes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void unlike(int filmId, int userId) {
        likes.getOrDefault(filmId, Collections.emptySet()).remove(userId);
    }
}
