package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class FilmDbStorage implements Storage<Film> {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_QUERY = "SELECT f.id, f.name, description, release_date, duration, mpa_id, " +
            "m.name mpa_name, genre_id, g.name genre_name FROM films f JOIN mpa m ON m.id = f.mpa_id " +
            "LEFT JOIN films_genres fg ON f.id = fg.film_id LEFT JOIN genres g ON g.id = fg.genre_id ";

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        long id = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        if (film.getGenres() != null && film.getGenres().size() > 0) {
            addGenres(id, List.copyOf(film.getGenres()));
        }
        return getById(id).orElseThrow(() -> new NotFoundException(String.format("ID: %d not found", id)));
    }

    @Override
    public Film update(long id, Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        deleteGenre(id);
        if (film.getGenres().size() > 0) {
            addGenres(id, List.copyOf(film.getGenres()));
        }
        return getById(id).orElseThrow(() -> new NotFoundException(String.format("ID: %d not found", id)));
    }

    @Override
    public boolean delete(long id) {
        String sqlQuery = "DELETE FROM films WHERE id = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public List<Film> getAll() {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SELECT_QUERY);
        return FilmMapper.extractorFilm(rs);
    }

    @Override
    public Optional<Film> getById(long id) {
        String sqlQuery = SELECT_QUERY + "WHERE f.id = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlQuery, id);
        List<Film> films = FilmMapper.extractorFilm(rs);
        Film film = (films.size() == 0) ? null : films.get(0);
        return Optional.ofNullable(film);
    }

    @Override
    public List<Film> getByIdSet(Collection<Long> ids) {
        if (ids.size() == 0) {
            return List.of();
        }
        String setToSql = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = SELECT_QUERY + String.format("WHERE f.id IN (%s)", setToSql);
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, ids.toArray());
        return FilmMapper.extractorFilm(rs);
    }

    private void addGenres(Long filmId, List<Genre> genres) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO films_genres (film_id, genre_id) VALUES ( ?, ? )",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, filmId);
                        ps.setLong(2, genres.get(i).getId());
                    }

                    public int getBatchSize() {
                        return genres.size();
                    }
                });
    }

    private void deleteGenre(long id) {
        jdbcTemplate.update("DELETE FROM films_genres WHERE film_id = ?", id);
    }

}
