package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class GenreDbStorage implements Storage<Genre>{

    private static final RowMapper<Genre> GENRE_MAPPER = ((rs, rowNum) -> Genre.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .build());
    private static final ResultSetExtractor<Genre> EXTRACTOR_GENRE = (
            rs -> rs.next() ? GENRE_MAPPER.mapRow(rs, 1) : null);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre create(Genre genre) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("genres")
                .usingGeneratedKeyColumns("id");
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(genre);
        long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return getById(id);
    }

    @Override
    public Genre update(long id, Genre genre) {
        String sql = "UPDATE genres SET  name = ? WHERE id = ?";
        jdbcTemplate.update(sql, genre.getName(), genre.getId());
        return getById(id);
    }

    @Override
    public boolean delete(long id) {
        String sqlQuery = "delete from genres where id = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public List<Genre> getAll() {
        String sqlQuery = "SELECT * FROM genres";
        return jdbcTemplate.query(sqlQuery, GENRE_MAPPER);
    }

    @Override
    public Genre getById(long id) {
        String sqlQuery = "SELECT * FROM genres WHERE id = ?";
        return jdbcTemplate.query(sqlQuery, EXTRACTOR_GENRE, id);
    }

    @Override
    public List<Genre> getByIdSet(Collection<Long> ids) {
        String setToSql = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = String.format("SELECT * FROM genres WHERE id IN (%s)", setToSql);
        return jdbcTemplate.query(sql, GENRE_MAPPER, ids.toArray());
    }

    @Override
    public List<Genre> getByIdSet(Collection<Long> ids, boolean isSort) {
        if (isSort) {
            return ids.stream().map(this::getById).collect(Collectors.toList());
        } else {
            return getByIdSet(ids);
        }
    }
}
