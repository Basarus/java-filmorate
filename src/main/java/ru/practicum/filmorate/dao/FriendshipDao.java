package ru.practicum.filmorate.dao;


import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.practicum.filmorate.model.User;


import java.util.List;


@Repository
@RequiredArgsConstructor
public class FriendshipDao {
    private final JdbcTemplate jdbc;
    private static final RowMapper<User> M = (rs, n) -> {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setEmail(rs.getString("email"));
        u.setLogin(rs.getString("login"));
        u.setName(rs.getString("name"));
        u.setBirthday(rs.getDate("birthday").toLocalDate());
        return u;
    };

    public void add(int userId, int friendId) {
        jdbc.update("MERGE INTO friendships(user_id,friend_id) KEY(user_id,friend_id) VALUES (?,?)", userId, friendId);
    }

    public void remove(int userId, int friendId) {
        jdbc.update("DELETE FROM friendships WHERE user_id=? AND friend_id=?", userId, friendId);
    }

    public List<User> friendsOf(int userId) {
        return jdbc.query("SELECT u.* FROM friendships f JOIN users u ON u.id=f.friend_id WHERE f.user_id=? ORDER BY u.id", M, userId);
    }

    public List<User> commonFriends(int userA, int userB) {
        return jdbc.query("SELECT u.* FROM friendships a JOIN friendships b ON a.friend_id=b.friend_id JOIN users u ON u.id=a.friend_id WHERE a.user_id=? AND b.user_id=? ORDER BY u.id", M, userA, userB);
    }
}