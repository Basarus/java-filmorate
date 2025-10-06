package ru.practicum.filmorate.dao;

public class InMemoryMpaDao extends MpaDao {
    public InMemoryMpaDao() {
        super(null);
    }

    @Override
    public boolean exists(Integer id) {
        return id != null && id > 0;
    }
}
