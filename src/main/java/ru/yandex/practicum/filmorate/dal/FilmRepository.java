package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Repository
public class FilmRepository extends BaseRepository<Film> {

    private static final String FIND_ALL_QUERY = "SELECT f.*, m.id as mpa_id, m.name as mpa_name FROM films f JOIN mpa m ON f.mpa = m.id";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM films WHERE name = ?";
    private static final String INSERT_FILM_QUERY = "INSERT INTO films (name,description,release_Date,duration,mpa) VALUES (?,?,?,?,?)";
    private static final String FIND_GENRES_BY_FILM_ID = "SELECT * FROM genres g JOIN films_genres fg ON g.id = fg.genre_id  WHERE fg.film_id = ? ORDER BY g.id";
    private static final String FIND_BY_ID_QUERY = "SELECT f.*, m.id As mpa_id, m.name As mpa_name " +
            "FROM films f " +
            "JOIN mpa m ON f.mpa = m.id " +
            "WHERE f.id = ?";

    private static final String DELETE_FILM_GENRES_QUERY = "DELETE FROM films_genres WHERE film_id = ?";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?)";
private static final String UPDATE_FILM_QUERY = "UPDATE films set name=?,description =?,release_Date = ?, duration =?, mpa =? WHERE id=?";
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
        System.out.println("Saving film MPA: " + film.getMpa().getId());
        System.out.println("Film genres to save: " + film.getGenres());
        saveFilmGenres(film.getId(), film.getGenres());
        return findById(id).orElse(film);
    }
    public Optional<Film> findById(Long id) {
        Optional<Film> film = findOne(FIND_BY_ID_QUERY, id);
       // film.ifPresent(this::loadGenresForFilm);
        film.ifPresent(f -> {
            System.out.println("Loaded film: " + f.getId() + ", MPA: " +
                    (f.getMpa() != null ? f.getMpa().getId() + "-" + f.getMpa().getName() : "null"));
            this.loadGenresForFilm(f);
        });
        return film;
    }

    private void loadGenresForFilm(Film film) {
        List<Genre> genres = jdbc.query(FIND_GENRES_BY_FILM_ID, genreRowMapper, film.getId());
        film.setGenres(new LinkedHashSet<>(genres));
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
        System.out.println("=== UPDATE PARAMETERS ===");
        System.out.println("1. name: " + film.getName());
        System.out.println("2. description: " + film.getDescription());
        System.out.println("3. releaseDate: " + film.getReleaseDate());
        System.out.println("4. duration: " + film.getDuration());
        System.out.println("5. mpaId: " + film.getMpa().getId());
        System.out.println("6. filmId: " + film.getId());
        update(UPDATE_FILM_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        saveFilmGenres(film.getId(), film.getGenres());
        return findById(film.getId()).orElseThrow(() ->
                new RuntimeException("Film not found after update"));

    }
}
