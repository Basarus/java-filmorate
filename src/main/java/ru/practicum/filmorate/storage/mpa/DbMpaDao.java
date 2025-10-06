package ru.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.practicum.filmorate.dao.MpaDao;

@Repository
public class DbMpaDao extends MpaDao {

    public DbMpaDao(JdbcTemplate jdbc) {
        super(jdbc);
    }

    @Override
    public boolean exists(Integer id) {
        return id != null;
    }
}

