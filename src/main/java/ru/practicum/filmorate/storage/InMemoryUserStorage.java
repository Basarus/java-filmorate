package ru.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.practicum.filmorate.model.User;

@Component
public class InMemoryUserStorage extends BaseInMemoryStorage<User> implements UserStorage {
}
