package ru.yandex.practicum.filmorate.repository.DbRepositories;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
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
    private final DbFeedRepository dbFeedRepository;

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
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        List<Film> films = jdbc.query(FIND_ALL_FILMS_QUERY, filmRowMapper);
        for (Film film : films) {
            film.setGenres(dbGenreRepository.loadGenres(film));
            film.setLikes(loadLikes(film.getId())
                    .stream()
                    .map(user -> user.getId())
                    .collect(Collectors.toSet()));
        }
        return films;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        //Film film = new Film();
        checkFilmId(newFilm.getId());
        jdbc.update(UPDATE_FILM_QUERY, newFilm.getName(), newFilm.getDescription(), newFilm.getReleaseDate(), newFilm.getDuration(), newFilm.getId());
        newFilm.setGenres(dbGenreRepository.loadGenres(newFilm));
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
        film.setLikes(loadLikes(id)
                .stream()
                .map(User::getId)
                .collect(Collectors.toSet()));
        return film;
    }

    @Override
    public Film deleteFilmById(Long id) {
        checkFilmId(id);
        Film film = jdbc.queryForObject(FIND_FILM_BY_ID_QUERY, filmRowMapper, id);
        jdbc.update(DELETE_FILM_QUERY, id);

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
        dbFeedRepository.createUserEvent (userId,filmId,"LIKE","ADD");
        return film;
    }

    @Override
    public Film deleteLikeUser(Long filmId, Long userId) {
        checkFilmId(filmId);
        checkUserId(userId);
        dbFeedRepository.createUserEvent (userId,filmId,"LIKE","REMOVE");
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