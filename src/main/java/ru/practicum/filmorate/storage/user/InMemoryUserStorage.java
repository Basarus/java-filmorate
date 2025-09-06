package ru.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.practicum.filmorate.model.User;
import ru.practicum.filmorate.storage.BaseInMemoryStorage;

@Component
public class InMemoryUserStorage extends BaseInMemoryStorage<User> implements UserStorage {
}
