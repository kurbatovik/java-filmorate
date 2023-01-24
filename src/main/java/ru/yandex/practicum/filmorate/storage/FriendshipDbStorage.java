package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.util.*;

@Repository
public class FriendshipDbStorage implements FriendshipStorage {

    private static final RowMapper<Friendship> FRIENDSHIP_MAPPER = ((rs, rowNum) -> Friendship.builder()
            .id(rs.getLong("id"))
            .userId(rs.getLong("user_id"))
            .friendId(rs.getLong("friend_id"))
            .build());

    private static final ResultSetExtractor<Friendship> EXTRACTOR_FRIENDSHIP = (
            rs -> rs.next() ? FRIENDSHIP_MAPPER.mapRow(rs, 1) : null);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendshipDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Friendship create(Friendship friendship) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("friends")
                .usingGeneratedKeyColumns("id");
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(friendship);
        long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        friendship.setId(id);
        return friendship;
    }

    @Override
    public Friendship update(long id, Friendship friendship) {
        String sql = "UPDATE friends SET  user_id = ?, friend_id = ?, accepted = ? WHERE id = ?";
        jdbcTemplate.update(sql, friendship.getUserId(), friendship.getFriendId(),
                friendship.isAccepted(), friendship.getId());
        return friendship;
    }

    @Override
    public boolean delete(long id) {
        String sqlQuery = "DELETE FROM friends WHERE id = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public List<Friendship> getAll() {
        String sqlQuery = "SELECT * FROM friends";
        return jdbcTemplate.query(sqlQuery, FRIENDSHIP_MAPPER);
    }

    @Override
    public Optional<Friendship> getById(long id) {
        String sqlQuery = "SELECT * FROM friends WHERE id = ?";
        return Optional.ofNullable(jdbcTemplate.query(sqlQuery, EXTRACTOR_FRIENDSHIP, id));
    }

    @Override
    public List<Friendship> getByIdSet(Collection<Long> ids) {
        String setToSql = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = String.format("SELECT * FROM friendships WHERE id IN (%s)", setToSql);
        return jdbcTemplate.query(sql, FRIENDSHIP_MAPPER, ids.toArray());
    }

    @Override
    public List<User> addFriend(long userId, long friendId) {
        if (userId == friendId || getFriendshipId(userId, friendId).size() > 0) {
            return findFriendsByUserId(userId);
        }
        Map<String, Object> friendshipParameters = new HashMap<>();
        friendshipParameters.put("user_id", userId);
        friendshipParameters.put("friend_id", friendId);
        friendshipParameters.put("accepted", false);
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate).withTableName("friends")
                .usingGeneratedKeyColumns("id");
        insert.execute(friendshipParameters);
        return findFriendsByUserId(userId);
    }

    @Override
    public List<User> delFriend(long userId, long friendId) {
        Friendship friendship = getFriendship(userId, friendId);
        if (friendship == null) {
            return findFriendsByUserId(userId);
        }
        if (friendship.getUserId() == userId) {
            jdbcTemplate.update("DELETE FROM friends WHERE id = ?", friendship.getId());
        }
        return findFriendsByUserId(userId);
    }

    @Override
    public List<User> findFriendsByUserId(long userId) {
        String sql = "SELECT u.id, email, login, name, birthday FROM users u JOIN friends f ON u.id = f.friend_id " +
                "WHERE user_id = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, userId);
        return UserMapper.extractorUser(rs);
    }

    @Override
    public List<User> findCommonUsers(long userId, long otherId) {
        String sql = "SELECT u.id, u.name, u.email, u.login, u.birthday FROM USERS u, FRIENDS f, FRIENDS o " +
                "WHERE u.ID = f.FRIEND_ID AND u.ID = o.FRIEND_ID AND f.USER_ID = ? AND o.USER_ID = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, userId, otherId);
        return UserMapper.extractorUser(rs);
    }

    private List<Long> getFriendshipId(long userId, long friendId) {
        return jdbcTemplate.queryForList("SELECT id FROM friends " +
                "WHERE user_id = ? AND friend_id = ?", Long.class, userId, friendId);
    }

    private Friendship getFriendship(long userId, long friendId) {
        NamedParameterJdbcTemplate namedJdbc = new NamedParameterJdbcTemplate(jdbcTemplate);
        String sql = "SELECT * FROM friends " +
                "WHERE (user_id = :uId AND friend_id = :fId) OR (friend_id = :uId AND user_id = :fId)";
        MapSqlParameterSource sqlParameters = new MapSqlParameterSource();
        sqlParameters.addValue("uId", userId);
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
