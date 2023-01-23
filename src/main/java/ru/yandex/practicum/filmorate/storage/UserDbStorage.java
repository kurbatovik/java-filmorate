package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDbStorage implements Storage<User> {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userMapper = ((rs, rowNum) -> {
        long id = rs.getLong("id");
        return User.builder()
                .id(id)
                .email(rs.getString("email"))
                .name(rs.getString("name"))
                .login(rs.getString("login"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    });

    private final ResultSetExtractor<User> extractorUser = (
            rs -> rs.next() ? userMapper.mapRow(rs, 1) : null);

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
        return jdbcTemplate.query(sqlQuery, userMapper);
    }

    @Override
    public Optional<User> getById(long id) {
        String sqlQuery = "SELECT * FROM users WHERE id = ?";
        return Optional.ofNullable(jdbcTemplate.query(sqlQuery, extractorUser, id));
    }

    @Override
    public List<User> getByIdSet(Collection<Long> ids) {
        String setToSql = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = String.format("SELECT * FROM users WHERE id IN (%s)", setToSql);
        return jdbcTemplate.query(sql, userMapper, ids.toArray());
    }
}
