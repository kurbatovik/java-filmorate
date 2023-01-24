package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class LikeDbStorage implements LikeStorage {

    private static final RowMapper<Like> LIKE_MAPPER = ((rs, rowNum) -> Like.builder()
            .id(rs.getLong("id"))
            .userId(rs.getLong("user_id"))
            .filmId(rs.getLong("film_id"))
            .build());
    private static final ResultSetExtractor<Like> EXTRACTOR_LIKE = (
            rs -> rs.next() ? LIKE_MAPPER.mapRow(rs, 1) : null);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Like create(Like like) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("likes")
                .usingGeneratedKeyColumns("id");
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(like);
        long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        like.setId(id);
        return like;
    }

    @Override
    public Like update(long id, Like like) {
        String sql = "UPDATE likes SET  film_id = ?, user_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, like.getFilmId(), like.getUserId(), like.getId());
        return like;
    }

    @Override
    public boolean delete(long id) {
        String sqlQuery = "DELETE FROM likes WHERE id = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public List<Like> getAll() {
        String sqlQuery = "SELECT * FROM likes";
        return jdbcTemplate.query(sqlQuery, LIKE_MAPPER);
    }

    @Override
    public Optional<Like> getById(long id) {
        String sqlQuery = "SELECT * FROM likes WHERE id = ?";
        return Optional.ofNullable(jdbcTemplate.query(sqlQuery, EXTRACTOR_LIKE, id));
    }

    @Override
    public List<Like> getByIdSet(Collection<Long> ids) {
        String setToSql = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = String.format("SELECT * FROM likes WHERE id IN (%s)", setToSql);
        return jdbcTemplate.query(sql, LIKE_MAPPER, ids.toArray());
    }

    @Override
    public void addLike(long filmId, long userId) {
        if (getLikesByFilmId(filmId).contains(userId)) {
            return;
        }
        String sqlQuery = "INSERT INTO likes (film_id, user_id) VALUES ( ?, ? )";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public List<Long> getLikesByFilmId(long filmId) {
        String query = "SELECT user_id FROM likes WHERE film_id = ?";
        return jdbcTemplate.queryForList(query, Long.class, filmId);
    }

    @Override
    public void delLike(long filmId, long userId) {
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?", filmId, userId);
    }

    @Override
    public List<Film> getPopular(int count) {
        String sql = "SELECT f.id,f.name, description, release_date, duration, mpa_id, m.name mpa_name, genre_id, g.name" +
                " genre_name FROM (SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, " +
                "COUNT(l.user_id) count FROM films f LEFT JOIN likes l ON f.id = l.film_id GROUP BY f.id ORDER BY count " +
                "DESC LIMIT ?) f JOIN mpa m ON m.id = f.mpa_id LEFT JOIN films_genres fg ON f.id = fg.film_id LEFT JOIN " +
                "genres g ON g.id = fg.genre_id ORDER BY count DESC ";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, count);
        return FilmMapper.extractorFilm(rs);
    }
}
