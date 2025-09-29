package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.mappers.GenreRowMapper;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Repository
@AllArgsConstructor
public class DbGenreRepository {

    private final JdbcTemplate jdbc;
    private final GenreRowMapper genreRowMapper;

    private static final String GET_ALL_GENRES_QUERY = "SELECT * FROM genres ORDER BY id ASC;";
    private static final String GET_GENRE_BY_ID_QUERY = "SELECT * FROM genres WHERE id =?;";
    private static final String IF_GENRE_EXISTS_QUERY = "SELECT COUNT(*) FROM genres where id=?;";
    private static final String GET_GENRES_FOR_FILM_QUERY = "SELECT * FROM genres JOIN genres_films" +
            " ON genres.id = genres_films.genre_id WHERE film_id = ? ORDER BY genres.id ASC";

    public Collection<Genre> getGenres() {
        List<Genre> genres = jdbc.query(GET_ALL_GENRES_QUERY, genreRowMapper);
        return genres;
    }

    public Genre getGenresById(Long id) {
        checkGenreId(id);
        Genre genre = jdbc.queryForObject(GET_GENRE_BY_ID_QUERY, genreRowMapper, id);
        return genre;
    }

    public void checkGenreId(Long id) {
        if (jdbc.queryForObject(IF_GENRE_EXISTS_QUERY, Integer.class, id) == 0 || jdbc.queryForObject(IF_GENRE_EXISTS_QUERY, Integer.class, id) == null) {
            throw new NotFoundException("Genre with id " + id + " not found");
        }
    }

    public Set<Genre> loadGenres(Film film) {
        List<Genre> genres = jdbc.query(GET_GENRES_FOR_FILM_QUERY, genreRowMapper, film.getId());
        Set<Genre> genres1 = new LinkedHashSet<>(genres);
        return genres1;
    }
}