package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.*;

public class FilmMapper {

    public static List<Film> extractorFilm(SqlRowSet rs){
        Map<Long, Film> filmsById = new LinkedHashMap<>();
        while (rs.next()) {
            long id = rs.getLong("id");
            Film film = filmsById.get(id);
            if (film == null) {
                film = buildFilm(rs, id);
                filmsById.put(film.getId(), film);
            }
            if (rs.getLong("GENRE_ID") > 0) {
                Genre genre = buildGenre(rs);
                film.getGenres().add(genre);
            }
        }
        return new ArrayList<>(filmsById.values());
    }

    private static Genre buildGenre(SqlRowSet rs) {
        return Genre.builder()
                .id(rs.getLong("genre_id"))
                .name(rs.getString("GENRE_NAME")).build();
    }

    private static Film buildFilm(SqlRowSet rs, long id){
        Film film = Film.builder()
                .id(id)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .build();
        MPA mpa = MPA.builder().id(rs.getLong("mpa_id"))
                .name(rs.getString("MPA_NAME")).build();
        film.setMpa(mpa);
        return film;
    }
}
