package ru.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Repository("userDbStorage")
@Qualifier("userDbStorage")
@Profile("!test")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;

    public UserDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<User> M = (rs, n) -> {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setEmail(rs.getString("email"));
        u.setLogin(rs.getString("login"));
        u.setName(rs.getString("name"));
        u.setBirthday(rs.getDate("birthday").toLocalDate());
        return u;
    };

    @Override
    public User save(User u) {
        jdbc.update(
                "INSERT INTO users(email, login, name, birthday) VALUES (?,?,?,?)",
                u.getEmail(), u.getLogin(), u.getName(), java.sql.Date.valueOf(u.getBirthday())
        );
        Integer id = jdbc.queryForObject("SELECT MAX(id) FROM users", Integer.class);
        u.setId(id);
        return u;
    }

    @Override
    public User update(User u) {
        jdbc.update(
                "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE id=?",
                u.getEmail(), u.getLogin(), u.getName(),
                java.sql.Date.valueOf(u.getBirthday()), u.getId()
        );
        return u;
    }

    @Override
    public Optional<User> findById(int id) {
        return jdbc.query("SELECT * FROM users WHERE id=?", M, id).stream().findFirst();
    }

    @Override
    public List<User> findAll() {
        return jdbc.query("SELECT * FROM users ORDER BY id", M);
    }

    @Override
    public boolean exists(int id) {
        Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE id=?", Integer.class, id);
        return c != null && c > 0;
    }
}
