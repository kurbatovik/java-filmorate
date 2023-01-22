package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FilmDbStorage implements Storage<Film>{

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
        if (film.getGenres()!= null && film.getGenres().size() > 0) {
            film.getGenres().stream()
                    .map(Genre::getId)
                    .forEach(genreId -> addGenre(id, genreId));
        }
        return getById(id);
    }

    @Override
    public Film update(long id, Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        deleteGenre(id);
        if (film.getGenres().size() > 0) {
            film.getGenres().forEach(genre -> addGenre(id, genre.getId()));
        }
        return getById(id);
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
    public Film getById(long id) {
        String sqlQuery = SELECT_QUERY + "WHERE f.id = ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, extractorFilm, id);
        if (films != null && films.size() > 0) {
            return films.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<Film> getByIdSet(Collection<Long> ids) {
        String setToSql = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = SELECT_QUERY + String.format("WHERE f.id IN (%s)", setToSql);
        return jdbcTemplate.query(sql, extractorFilm, ids.toArray());
    }

    @Override
    public List<Film> getByIdSet(Collection<Long> ids, boolean isSort) {
        if (isSort) {
            return ids.stream().map(this::getById).collect(Collectors.toList());
        } else {
            return getByIdSet(ids);
        }
    }

    public void addLike(long id, long userId){
        if (getLikes(id).contains(userId)) {
            return;
        }
        String sqlQuery = "INSERT INTO likes (film_id, user_id) VALUES ( ?, ? )";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    public List<Long> getLikes(long id){
        String query = "SELECT USER_ID FROM likes WHERE film_id = ?";
        return jdbcTemplate.queryForList(query, Long.class, id);
    }

    public void delLike(long id, long userId) {
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?", id, userId);
    }

    public List<Long> getPopular(int count) {
        String sql = "SELECT f.id FROM films f LEFT JOIN likes l ON f.id = l.film_id " +
                "GROUP BY f.id ORDER BY COUNT(l.user_id) DESC LIMIT ?";
        return jdbcTemplate.queryForList(sql, Long.class, count);
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
        film.getLikes().addAll(getLikes(id));
        return film;
    }

    private void addGenre(Long filmId, long genreId) {
        if (checkGenre(filmId, genreId)) {
            return;
        }
        String sqlQuery = "INSERT INTO films_genres (film_id, genre_id) VALUES ( ?, ? )";
        jdbcTemplate.update(sqlQuery, filmId, genreId);
    }

    private void deleteGenre(long id) {
        jdbcTemplate.update("DELETE FROM films_genres WHERE film_id = ?", id);
    }

    private boolean checkGenre(long filmId, long genreId) {
        SqlRowSet filmGenreRow = jdbcTemplate.queryForRowSet("SELECT * FROM films_genres " +
                "WHERE film_id = ? AND genre_id = ?", filmId, genreId);
        return filmGenreRow.next();
    }
}
