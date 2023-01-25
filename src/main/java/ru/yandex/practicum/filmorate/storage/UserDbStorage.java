package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDbStorage implements Storage<User> {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        long id = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        user.setId(id);
        return user;
    }

    @Override
    public User update(long id, User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public boolean delete(long id) {
        String sqlQuery = "DELETE FROM users WHERE id = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "SELECT * FROM users";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlQuery);
        return UserMapper.extractorUser(rs);
    }

    @Override
    public Optional<User> getById(long id) {
        String sqlQuery = "SELECT * FROM users WHERE id = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlQuery, id);
        List<User> users = UserMapper.extractorUser(rs);
        User user = (users.size() > 0) ? users.get(0) : null;
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> getByIdSet(Collection<Long> ids) {
        String setToSql = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = String.format("SELECT * FROM users WHERE id IN (%s)", setToSql);
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, ids.toArray());
        return UserMapper.extractorUser(rs);
    }
}
