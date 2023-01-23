package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class FilmDbStorage implements Storage<Film> {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_QUERY = "SELECT f.id,f.name,description,release_date,duration,mpa_id,mpa_name," +
            " genre_id, g.name genre_name " +
            "FROM (SELECT f.id, f.name, description, release_date, duration, mpa_id, m.name mpa_name" +
            "      FROM films f" +
            "               JOIN mpa m ON m.id = f.mpa_id) f" +
            "         LEFT JOIN films_genres fg ON f.id = fg.film_id" +
            "         LEFT JOIN genres g ON g.id = fg.genre_id ";

    private final ResultSetExtractor<List<Film>> extractorFilm = rs -> {
        Map<Long, Film> filmsById = new HashMap<>();
        while (rs.next()) {
            long id = rs.getLong("id");
            Film film = filmsById.get(id);
            if (film == null) {
                film = createFilm(rs, id);
                filmsById.put(film.getId(), film);
            }
            if (rs.getLong("genre_id") > 0) {
                Genre genre = Genre.builder()
                        .id(rs.getLong("genre_id"))
                        .name(rs.getString("genre_name")).build();
                film.getGenres().add(genre);
            }
        }
        return new ArrayList<>(filmsById.values());
    };

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
        film.setId(id);
        return film;
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
        return film;
    }

    @Override
    public boolean delete(long id) {
        String sqlQuery = "DELETE FROM films WHERE id = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public List<Film> getAll() {
        return jdbcTemplate.query(SELECT_QUERY, extractorFilm);
    }

    @Override
    public Optional<Film> getById(long id) {
        String sqlQuery = SELECT_QUERY + "WHERE f.id = ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, extractorFilm, id);
        Film film = (films == null || films.size() == 0) ? null : films.get(0);
        return Optional.ofNullable(film);
    }

    @Override
    public List<Film> getByIdSet(Collection<Long> ids) {
        if (ids.size() == 0) {
            return List.of();
        }
        String setToSql = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = SELECT_QUERY + String.format("WHERE f.id IN (%s)", setToSql);
        return jdbcTemplate.query(sql, extractorFilm, ids.toArray());
    }


    private Film createFilm(ResultSet rs, long id) throws SQLException {
        Film film = Film.builder()
                .id(id)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .build();
        MPA mpa = MPA.builder().id(rs.getLong("mpa_id"))
                .name(rs.getString("mpa_name")).build();
        film.setMpa(mpa);
        return film;
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
