package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.MpaRowMapper;
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
    private final GenreRowMapper genreRowMapper;
    private final MpaRowMapper  mpaRowMapper;
    private final UserRowMapper userRowMapper;

    private final static String FIND_ALL_FILMS_QUERY = "SELECT films.*, mpa.name as mpa_name FROM films" +
            " LEFT JOIN mpa ON films.mpa = mpa.id ORDER BY films.id ASC;";
    private final static String UPDATE_FILM_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? WHERE id =?";
    private final static String FIND_FILM_BY_ID_QUERY = "SELECT films.*, mpa.name as mpa_name FROM films" +
            " LEFT JOIN mpa ON films.mpa = mpa.id WHERE films.id = ?;";
    private final static String DELETE_FILM_QUERY = "DELETE FROM films WHERE id =?;";
    private final static String ADD_LIKE_TO_FILM_QUERY = "INSERT INTO films_likes (user_id, film_id) VALUES (?, ?);";
    private final static String DELETE_LIKE_FROM_FILM_QUERY = "DELETE FROM films_likes where user_id =? AND film_id = ?;";
    private final static String GET_POPULAR_FILMS_QUERY = "SELECT films.*, mpa.name as mpa_name, COUNT(films_likes.user_id) as likes_count FROM films " +
            "LEFT JOIN films_likes ON films.id = films_likes.film_id " +
            "LEFT JOIN mpa ON films.mpa = mpa.id GROUP BY films.id, mpa.name " +
            "ORDER BY COUNT(films_likes.user_id) DESC LIMIT ?";
    private final static String GET_ALL_GENRES_QUERY = "SELECT * FROM genres ORDER BY id ASC;";
    private final static String GET_GENRE_BY_ID_QUERY = "SELECT * FROM genres WHERE id =?;";
    private final static String GET_ALL_MPA_QUERY = "SELECT * FROM mpa ORDER BY id ASC;";
    private final static String GET_MPA_BY_ID_QUERY = "SELECT * FROM mpa WHERE id = ?;";
    private final static String IF_MPA_EXISTS_QUERY = "SELECT COUNT(*) FROM mpa where id=?;";
    private final static String IF_FILM_EXISTS_QUERY = "SELECT COUNT(*) FROM films WHERE id = ?;";
    private final static String IF_USER_EXISTS_QUERY = "SELECT COUNT(*) FROM users where id=?;";
    private final static String IF_GENRE_EXISTS_QUERY = "SELECT COUNT(*) FROM genres where id=?;";
    private final static String GET_GENRES_FOR_FILM_QUERY = "SELECT * FROM genres JOIN genres_films" +
            " ON genres.id = genres_films.genre_id WHERE film_id = ? ORDER BY genres.id ASC";
    private final static String GET_LIKES_FOR_FILM_QUERY = "SELECT users.* FROM films_likes JOIN users ON films_likes.user_id = users.id" +
            " WHERE films_likes.film_id = ?;";
    private final static String ADD_FILM_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa)" +
            " VALUES (?, ?, ?, ?, ?)";
    private final static String ADD_GENRES_TO_FILM_QUERY = "INSERT INTO genres_films (genre_id, film_id) VALUES (?, ?)";


    @Override
    public Film addFilm(Film film) {
        checkMpaId(film.getMpa().getId());
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
                checkGenreId(genre.getId());
                jdbc.update(ADD_GENRES_TO_FILM_QUERY, genre.getId(), film.getId());
            }
            film.setGenres(loadGenres(film));
        }
        if (!(film.getMpa() == null)){
            film.setMpa(getMpaById(film.getMpa().getId()));
    }
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        List <Film> films = jdbc.query(FIND_ALL_FILMS_QUERY, filmRowMapper);
        for (Film film : films) {
            film.setGenres(loadGenres(film));
            film.setLikes(loadLikes(film.getId())
                    .stream()
                    .map(user ->  user.getId())
                    .collect(Collectors.toSet()));
        }
        return films;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        Film film = new Film();
        checkFilmId(newFilm.getId());
        jdbc.update(UPDATE_FILM_QUERY, newFilm.getName(), newFilm.getDescription(), newFilm.getReleaseDate(), newFilm.getDuration(), newFilm.getId());
        newFilm.setGenres(loadGenres(newFilm));
        newFilm.setMpa(getMpaById(newFilm.getMpa().getId()));
        newFilm.setLikes(loadLikes(newFilm.getId()).stream().map(user -> user.getId()).collect(Collectors.toSet()));
        return newFilm;
    }

    @Override
    public Film getFilmById(Long id) {
        checkFilmId(id);
        Film film = jdbc.queryForObject(FIND_FILM_BY_ID_QUERY, filmRowMapper, id);
        if (!(loadGenres(film).size() == 0)) {
            film.setGenres(loadGenres(film));
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
        Film film = jdbc.queryForObject(DELETE_FILM_QUERY,filmRowMapper,id);
        return film;
    }

    @Override
    public Film likeFilmById(Long filmId, Long userId) {
        checkFilmId(filmId);
        checkUserId(userId);

        jdbc.update(ADD_LIKE_TO_FILM_QUERY, userId, filmId);
        //String sql1 = "SELECT films.*, mpa.name as mpa_name FROM films" +
          //      " LEFT JOIN mpa ON films.mpa = mpa.id WHERE films.id = ?;";
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
        List <Film> popularFilms = jdbc.query(GET_POPULAR_FILMS_QUERY, filmRowMapper, count);
        return popularFilms;
    }

    @Override
    public Collection<Genre> getGenres() {
        List <Genre> genres = jdbc.query(GET_ALL_GENRES_QUERY, genreRowMapper);
        return genres;
    }

    @Override
    public Genre getGenresById(Long id) {
        checkGenreId(id);
        Genre genre  = jdbc.queryForObject(GET_GENRE_BY_ID_QUERY, genreRowMapper,id);
        return genre;
    }

    @Override
    public Collection<Mpa> getMpas() {
        List<Mpa> mpas = jdbc.query(GET_ALL_MPA_QUERY,mpaRowMapper);
        return mpas;
    }

    @Override
    public Mpa getMpaById(Long id) {
        checkMpaId(id);
        Mpa mpa = jdbc.queryForObject(GET_MPA_BY_ID_QUERY,mpaRowMapper, id);
        return mpa;
    }

    public void checkMpaId (Long id){
        if (jdbc.queryForObject(IF_MPA_EXISTS_QUERY,Integer.class,id) == 0){
            throw new NotFoundException("Mpa with id " + id + " not found");
        };
    }

    public void checkFilmId(Long id){
        if (jdbc.queryForObject(IF_FILM_EXISTS_QUERY,Integer.class,id) == 0){
            throw new NotFoundException("Film with id " + id + " not found");
        }
    }

    public void checkUserId(Long id){
        if (jdbc.queryForObject(IF_USER_EXISTS_QUERY,Integer.class,id) == 0){
            throw new NotFoundException("User with id " + id + " not found");
        }
    }

    public void checkGenreId(Long id){
        if (jdbc.queryForObject(IF_GENRE_EXISTS_QUERY,Integer.class,id) == 0 || jdbc.queryForObject(IF_GENRE_EXISTS_QUERY,Integer.class,id) == null){
            throw new NotFoundException("Genre with id " + id + " not found");
        }
    }

    public Set<Genre> loadGenres(Film film){
        List<Genre> genres = jdbc.query(GET_GENRES_FOR_FILM_QUERY, genreRowMapper, film.getId());
        Set<Genre> genres1 = new LinkedHashSet<>(genres);
        return genres1;
    }

    public Set<User> loadLikes(Long filmId){
        List <User> likes = jdbc.query(GET_LIKES_FOR_FILM_QUERY, userRowMapper, filmId);
        Set <User> likes1 = new LinkedHashSet<>(likes);
        return likes1;
    }
}
