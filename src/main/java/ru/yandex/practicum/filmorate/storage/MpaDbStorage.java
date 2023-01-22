package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MpaDbStorage implements Storage<MPA>{

    private static final RowMapper<MPA> MPA_MAPPER = ((rs, rowNum) -> MPA.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .build());
    private static final ResultSetExtractor<MPA> EXTRACTOR_MPA = (
            rs -> rs.next() ? MPA_MAPPER.mapRow(rs, 1) : null);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MPA create(MPA mpa) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("mpa")
                .usingGeneratedKeyColumns("id");
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(mpa);
        long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return getById(id);
    }

    @Override
    public MPA update(long id, MPA mpa) {
        String sql = "UPDATE mpa SET  name = ? WHERE id = ?";
        jdbcTemplate.update(sql, mpa.getName(), mpa.getId());
        return getById(id);
    }

    @Override
    public boolean delete(long id) {
        String sqlQuery = "delete from mpa where id = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public List<MPA> getAll() {
        String sqlQuery = "SELECT * FROM mpa";
        return jdbcTemplate.query(sqlQuery, MPA_MAPPER);
    }

    @Override
    public MPA getById(long id) {
        String sqlQuery = "SELECT * FROM mpa WHERE id = ?";
        return jdbcTemplate.query(sqlQuery, EXTRACTOR_MPA, id);
    }

    @Override
    public List<MPA> getByIdSet(Collection<Long> ids) {
        String setToSql = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = String.format("SELECT * FROM mpa WHERE id IN (%s)", setToSql);
        return jdbcTemplate.query(sql, MPA_MAPPER, ids.toArray());
    }

    @Override
    public List<MPA> getByIdSet(Collection<Long> ids, boolean isSort) {
        if (isSort) {
            return ids.stream().map(this::getById).collect(Collectors.toList());
        } else {
            return getByIdSet(ids);
        }
    }
}
