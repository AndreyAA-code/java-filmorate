package ru.yandex.practicum.filmorate.repository.DbRepositories;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
@Primary
public class DbFilmRepository implements FilmRepository {

    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmRowMapper;
    private final UserRowMapper userRowMapper;
    private final DbMpaRepository dbMpaRepository;
    private final DbGenreRepository dbGenreRepository;
    private final DbDirectorRepository dbDirectorRepository;

    private static final String FIND_ALL_FILMS_QUERY = "SELECT films.*, mpa.name as mpa_name FROM films" +
            " LEFT JOIN mpa ON films.mpa_id = mpa.id ORDER BY films.id ASC;";
    private static final String UPDATE_FILM_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? WHERE id =?";
    private static final String FIND_FILM_BY_ID_QUERY = "SELECT films.*, mpa.name as mpa_name FROM films" +
            " LEFT JOIN mpa ON films.mpa_id = mpa.id WHERE films.id = ?;";
    private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE id =?;";
    private static final String ADD_LIKE_TO_FILM_QUERY = "INSERT INTO films_likes (user_id, film_id) VALUES (?, ?);";
    private static final String DELETE_LIKE_FROM_FILM_QUERY = "DELETE FROM films_likes where user_id =? AND film_id = ?;";
    private static final String GET_POPULAR_FILMS_QUERY = "SELECT films.*, mpa.name as mpa_name, COUNT(films_likes.user_id) as likes_count FROM films " +
            "LEFT JOIN films_likes ON films.id = films_likes.film_id " +
            "LEFT JOIN mpa ON films.mpa_id = mpa.id GROUP BY films.id, mpa.name " +
            "ORDER BY COUNT(films_likes.user_id) DESC LIMIT ?";
    private static final String IF_FILM_EXISTS_QUERY = "SELECT COUNT(*) FROM films WHERE id = ?;";
    private static final String IF_USER_EXISTS_QUERY = "SELECT COUNT(*) FROM users where id=?;";
    private static final String GET_LIKES_FOR_FILM_QUERY = "SELECT users.* FROM films_likes JOIN users ON films_likes.user_id = users.id" +
            " WHERE films_likes.film_id = ?;";
    private static final String ADD_FILM_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_id)" +
            " VALUES (?, ?, ?, ?, ?)";
    private static final String ADD_GENRES_TO_FILM_QUERY = "INSERT INTO genres_films (genre_id, film_id) VALUES (?, ?)";
    private static final String ADD_DIRECTORS_TO_FILM_QUERY = "INSERT INTO directors_films (director_id, film_id) VALUES (?, ?)";
    private static final String DELETE_FILM_DIRECTORS_QUERY = "DELETE FROM directors_films WHERE film_id = ?";
    private static final String GET_FILMS_BY_DIRECTOR_QUERY_YEAR_SORTED = "SELECT f.*, m.name as mpa_name " +
            "FROM films f LEFT JOIN mpa m ON f.mpa_id = m.id " +
            "JOIN directors_films df ON f.id = df.film_id " +
            "WHERE df.director_id = ? ORDER BY f.release_date";
    private static final String GET_FILMS_BY_DIRECTOR_QUERY_LIKES_SORTED = "SELECT f.*, m.name as mpa_name " +
            "FROM films f LEFT JOIN mpa m ON f.mpa_id = m.id " +
            "LEFT JOIN films_likes fl ON f.id = fl.film_id " +
            "JOIN directors_films df ON f.id = df.film_id " +
            "WHERE df.director_id = ? GROUP BY f.id ORDER BY COUNT(fl.user_id) DESC";

    @Override
    public Film addFilm(Film film) {
        dbMpaRepository.checkMpaId(film.getMpa().getId());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(ADD_FILM_QUERY, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        Long generatedId = keyHolder.getKey().longValue();
        film.setId(generatedId);
        if (!(film.getGenres() == null)) {
            for (Genre genre : film.getGenres()) {
                dbGenreRepository.checkGenreId(genre.getId());
                jdbc.update(ADD_GENRES_TO_FILM_QUERY, genre.getId(), film.getId());
            }
            film.setGenres(dbGenreRepository.loadGenres(film));
        }
        if (!(film.getMpa() == null)) {
            film.setMpa(dbMpaRepository.getMpaById(film.getMpa().getId()));
        }
        if (!(film.getDirectors() == null)) {
            for (Director director : film.getDirectors()) {
                dbDirectorRepository.checkDirectorId(director.getId());
                jdbc.update(ADD_DIRECTORS_TO_FILM_QUERY, director.getId(), film.getId());
            }
            film.setDirectors(dbDirectorRepository.loadDirectors(film.getId()));
        }
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        List<Film> films = jdbc.query(FIND_ALL_FILMS_QUERY, filmRowMapper);
        for (Film film : films) {
            film.setGenres(dbGenreRepository.loadGenres(film));
            film.setDirectors(dbDirectorRepository.loadDirectors(film.getId()));
            film.setLikes(loadLikes(film.getId())
                    .stream()
                    .map(user -> user.getId())
                    .collect(Collectors.toSet()));
        }
        return films;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        checkFilmId(newFilm.getId());
        jdbc.update(UPDATE_FILM_QUERY, newFilm.getName(), newFilm.getDescription(), newFilm.getReleaseDate(), newFilm.getDuration(), newFilm.getId());
        if (!(newFilm.getDirectors() == null)) {
            jdbc.update(DELETE_FILM_DIRECTORS_QUERY, newFilm.getId());
            for (Director director : newFilm.getDirectors()) {
                dbDirectorRepository.checkDirectorId(director.getId());
                jdbc.update(ADD_DIRECTORS_TO_FILM_QUERY, director.getId(), newFilm.getId());
            }
            newFilm.setDirectors(dbDirectorRepository.loadDirectors(newFilm.getId()));
        }
        newFilm.setGenres(dbGenreRepository.loadGenres(newFilm));
        newFilm.setDirectors(dbDirectorRepository.loadDirectors(newFilm.getId()));
        newFilm.setMpa(dbMpaRepository.getMpaById(newFilm.getMpa().getId()));
        newFilm.setLikes(loadLikes(newFilm.getId()).stream().map(user -> user.getId()).collect(Collectors.toSet()));
        return newFilm;
    }

    @Override
    public Film getFilmById(Long id) {
        checkFilmId(id);
        Film film = jdbc.queryForObject(FIND_FILM_BY_ID_QUERY, filmRowMapper, id);
        if (!(dbGenreRepository.loadGenres(film).size() == 0)) {
            film.setGenres(dbGenreRepository.loadGenres(film));
        }
        film.setDirectors(dbDirectorRepository.loadDirectors(id));
        film.setLikes(loadLikes(id)
                .stream()
                .map(User::getId)
                .collect(Collectors.toSet()));
        return film;
    }

    @Override
    public Film deleteFilmById(Long id) {
        checkFilmId(id);
        Film film = jdbc.queryForObject(DELETE_FILM_QUERY, filmRowMapper, id);
        return film;
    }

    @Override
    public Film likeFilmById(Long filmId, Long userId) {
        checkFilmId(filmId);
        checkUserId(userId);

        jdbc.update(ADD_LIKE_TO_FILM_QUERY, userId, filmId);
        Film film = jdbc.queryForObject(FIND_FILM_BY_ID_QUERY, filmRowMapper, filmId);
        film.setLikes(loadLikes(filmId)
                .stream()
                .map(User::getId)
                .collect(Collectors.toSet()));
        return film;
    }

    @Override
    public Film deleteLikeUser(Long filmId, Long userId) {
        checkFilmId(filmId);
        checkUserId(userId);
        jdbc.update(DELETE_LIKE_FROM_FILM_QUERY, userId, filmId);
        Film film = jdbc.queryForObject(FIND_FILM_BY_ID_QUERY, filmRowMapper, filmId);
        film.setLikes(loadLikes(filmId)
                .stream()
                .map(User::getId)
                .collect(Collectors.toSet()));
        return film;
    }

    @Override
    public Collection<Film> getPopularFilms(Long count) {
        List<Film> popularFilms = jdbc.query(GET_POPULAR_FILMS_QUERY, filmRowMapper, count);
        return popularFilms;
    }

    @Override
    public Collection<Film> getFilmsByDirector(Long directorId, String sortBy) {
        dbDirectorRepository.checkDirectorId(directorId);
        List<Film> filmsByDirector;
        switch (sortBy) {
            case "year" ->
                    filmsByDirector = jdbc.query(GET_FILMS_BY_DIRECTOR_QUERY_YEAR_SORTED, filmRowMapper, directorId);
            case "likes" ->
                    filmsByDirector = jdbc.query(GET_FILMS_BY_DIRECTOR_QUERY_LIKES_SORTED, filmRowMapper, directorId);
            default -> throw new NotFoundException("unknown request parameter");
        }
        for (Film film : filmsByDirector) {
            film.setGenres(dbGenreRepository.loadGenres(film));
            film.setDirectors(dbDirectorRepository.loadDirectors(film.getId()));
            film.setLikes(loadLikes(film.getId())
                    .stream()
                    .map(User::getId)
                    .collect(Collectors.toSet()));
        }
        return filmsByDirector;
    }

    private void checkFilmId(Long id) {
        if (jdbc.queryForObject(IF_FILM_EXISTS_QUERY, Integer.class, id) == 0) {
            throw new NotFoundException("Film with id " + id + " not found");
        }
    }

    private void checkUserId(Long id) {
        if (jdbc.queryForObject(IF_USER_EXISTS_QUERY, Integer.class, id) == 0) {
            throw new NotFoundException("User with id " + id + " not found");
        }
    }

    private Set<User> loadLikes(Long filmId) {
        List<User> likes = jdbc.query(GET_LIKES_FOR_FILM_QUERY, userRowMapper, filmId);
        Set<User> likes1 = new LinkedHashSet<>(likes);
        return likes1;
    }
}