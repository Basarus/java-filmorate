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
        System.out.println(">>> DEBUG like() in storage@" + System.identityHashCode(this) + " => " + likes);
    }

    @Override
    public void unlike(int filmId, int userId) {
        Set<Integer> filmLikes = likes.get(filmId);
        if (filmLikes != null) {
            filmLikes.remove(userId);
            if (filmLikes.isEmpty()) {
                likes.remove(filmId);
            }
        }
    }

    @Override
    public int countLikes(int filmId) {
        Set<Integer> filmLikes = likes.get(filmId);
        return filmLikes == null ? 0 : filmLikes.size();
    }

    @Override
    public Set<Integer> getLikes(int filmId) {
        System.out.println(">>> DEBUG getLikes() in storage@" + System.identityHashCode(this) + " => " + likes);
        return likes.getOrDefault(filmId, new HashSet<>());
    }
}
