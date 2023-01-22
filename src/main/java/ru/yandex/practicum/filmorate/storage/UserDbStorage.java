package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserDbStorage implements Storage<User> {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userMapper = ((rs, rowNum) -> {
        long id = rs.getLong("id");
        User user = User.builder()
                .id(id)
                .email(rs.getString("email"))
                .name(rs.getString("name"))
                .login(rs.getString("login"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
        user.getLikedFilms().addAll(getLikedFilms(id));
        user.getFriends().addAll(getFriendsIds(id));
        return user;
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
        return getById(id);
    }

    @Override
    public User update(long id, User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return getById(id);
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
    public User getById(long id) {
        String sqlQuery = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.query(sqlQuery, extractorUser, id);
    }

    @Override
    public List<User> getByIdSet(Collection<Long> ids) {
        String setToSql = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = String.format("SELECT * FROM users WHERE id IN (%s)", setToSql);
        return jdbcTemplate.query(sql, userMapper, ids.toArray());
    }

    @Override
    public List<User> getByIdSet(Collection<Long> ids, boolean isSort) {
        if (isSort) {
            return ids.stream().map(this::getById).collect(Collectors.toList());
        } else {
            return getByIdSet(ids);
        }
    }

    public User getByEmail(String email) {
        String sqlQuery = "SELECT * FROM users WHERE email = ?";
        return jdbcTemplate.query(sqlQuery, extractorUser, email);
    }

    public List<User> addFriend(long id, long friendId) {
        if (id == friendId || getFriendshipId(id, friendId).size() > 0) {
            return getByIdSet(getFriendsIds(id));
        }
        if (getFriendshipId(friendId, id).size() > 0) {
            long friendshipId = getFriendshipId(friendId, id).get(0);
            jdbcTemplate.update("UPDATE friends SET accepted = ? WHERE id = ?", true, friendshipId);
        } else {
            Map<String, Object> friendshipParameters = new HashMap<>();
            friendshipParameters.put("user_id", id);
            friendshipParameters.put("friend_id", friendId);
            friendshipParameters.put("accepted", false);
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate).withTableName("friends")
                    .usingGeneratedKeyColumns("id");
            insert.execute(friendshipParameters);
        }
        return getByIdSet(getFriendsIds(id));
    }

    public List<Long> delFriend(long id, long friendId) {
        Friendship friendship = getFriendship(id, friendId);
        if (friendship == null) {
            return getFriendsIds(id);
        }
        if (friendship.getFriendId() == id && friendship.isAccepted()) {
            jdbcTemplate.update("UPDATE friends SET accepted = ? WHERE id = ?", false, friendship.getId());
        }
        if (friendship.getUserId() == id) {
            jdbcTemplate.update("DELETE FROM friends WHERE id = ?", friendship.getId());
            if (friendship.isAccepted()) {
                addFriend(friendId, id);
            }
        }
        return getFriendsIds(id);
    }

    private List<Long> getFriendsIds(long id) {
        String sql = "SELECT friend_id FROM friends WHERE user_id = ? " +
                "UNION ALL " +
                "SELECT user_id FROM friends WHERE accepted = TRUE AND friend_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, id, id);
    }

    private Collection<Long> getLikedFilms(long id) {
        String sql = "SELECT film_id FROM likes WHERE user_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, id);
    }

    private List<Long> getFriendshipId(long id, long friendId) {
        return jdbcTemplate.queryForList("SELECT id FROM friends " +
                "WHERE user_id = ? AND friend_id = ?", Long.class, id, friendId);
    }

    private Friendship getFriendship(long id, long friendId) {
        NamedParameterJdbcTemplate namedJdbc = new NamedParameterJdbcTemplate(jdbcTemplate);
        String sql = "SELECT * FROM friends " +
                "WHERE (user_id = :uId AND friend_id = :fId) OR (friend_id = :uId AND user_id = :fId)";
        MapSqlParameterSource sqlParameters = new MapSqlParameterSource();
        sqlParameters.addValue("uId", id);
        sqlParameters.addValue("fId", friendId);
        RowMapper<Friendship> friendshipMapper = (rs, rowNum) -> Friendship.builder()
                .id(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .friendId(rs.getLong("friend_id"))
                .accepted(rs.getBoolean("accepted"))
                .build();
        List<Friendship> friendship = namedJdbc.query(sql, sqlParameters, friendshipMapper);
        return friendship.size() == 1 ? friendship.get(0) : null;
    }
}
