package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class FilmRepository extends BaseRepository<Film> {

    private static final String FIND_ALL_QUERY = "SELECT f.*, m.id, m.name FROM films f JOIN mpa m ON f.mpa = m.id";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM films WHERE name = ?";
    private static final String INSERT_FILM_QUERY = "INSERT INTO films (name,description,release_Date,duration,mpa) VALUES (?,?,?,?,?)";
    private static final String FIND_GENRES_BY_FILM_ID = "SELECT * FROM genres g " +
            "JOIN films_genres fg ON g.id = fg.genre_id " +
            "WHERE fg.film_id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT f.*, m.id, m.name " +
            "FROM films f " +
            "JOIN mpa m ON f.id = m.id " +
            "WHERE f.id = ?";

    private static final String DELETE_FILM_GENRES_QUERY = "DELETE FROM films_genres WHERE film_id = ?";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?)";
private static final String UPDATE_FILM_QUERY = "UPDATE films set name=?,description =?,release_Date = ?, duration =?, mpa =?) VALUES (?,?,?,?,?)";
    private final GenreRowMapper genreRowMapper;

    public FilmRepository (JdbcTemplate jdbc, RowMapper<Film> mapper, GenreRowMapper genreRowMapper) {
        super(jdbc, mapper);
        this.genreRowMapper = genreRowMapper;
    }

    public List<Film> findAll() {
        List<Film> films = findMany(FIND_ALL_QUERY);
        films.forEach(this::loadGenresForFilm);
        return films;
    }

    public Optional<Film> findByName(String name) {
        Optional<Film> film = findOne(FIND_BY_NAME_QUERY, name);
        film.ifPresent(this::loadGenresForFilm);
        return film;
    }

    public Film save(Film film) {
        System.out.println("=== SAVING FILM ===");
        System.out.println("Film MPA ID: " + film.getMpa().getId());
        System.out.println("Film genres: " + film.getGenres());
        long id = insert(INSERT_FILM_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        film.setId(id);
        System.out.println("Saving film ID: " + id);
        System.out.println("Film genres to save: " + film.getGenres());
        saveFilmGenres(film.getId(), film.getGenres());
        return findById(id).orElse(film);
    }
    public Optional<Film> findById(Long id) {
        Optional<Film> film = findOne(FIND_BY_ID_QUERY, id);
        film.ifPresent(this::loadGenresForFilm);
        return film;
    }

    private void loadGenresForFilm(Film film) {
        List<Genre> genres = jdbc.query(FIND_GENRES_BY_FILM_ID, genreRowMapper, film.getId());
        film.setGenres(new HashSet<>(genres));
    }

    private void saveFilmGenres(Long filmId, Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }
        // Remove existing genres
        jdbc.update(DELETE_FILM_GENRES_QUERY, filmId);

        // Add new genres
        for (Genre genre : genres) {
            jdbc.update(INSERT_FILM_GENRE_QUERY, filmId, genre.getId());
        }
    }
    public Film update(Film film) {
        update(INSERT_FILM_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        return film;
    }
}
