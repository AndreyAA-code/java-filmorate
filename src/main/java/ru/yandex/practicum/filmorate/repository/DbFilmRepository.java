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
@Primary
@AllArgsConstructor

public class DbFilmRepository implements FilmRepository {

    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmRowMapper;
    private final GenreRowMapper genreRowMapper;
    private final MpaRowMapper  mpaRowMapper;
    private final UserRowMapper userRowMapper;

    @Override
    public Film addFilm(Film film) {
        checkMpaId(film.getMpa().getId());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa)" +
                " VALUES (?, ?, ?, ?, ?)";
        String sql1 = "INSERT INTO genres_films (genre_id, film_id) VALUES (?, ?)";

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
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
                jdbc.update(sql1, genre.getId(), film.getId());
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
        String sql = "SELECT films.*, mpa.name as mpa_name FROM films" +
                " LEFT JOIN mpa ON films.mpa = mpa.id ORDER BY films.id ASC;";
        List <Film> films = jdbc.query(sql, filmRowMapper);
        for (Film film : films) {
            film.setGenres(loadGenres(film));
            film.setLikes(loadLikes(film.getId()).stream().map(user ->  user.getId()).collect(Collectors.toSet()));
        }

        return films;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        Film film = new Film();
        checkFilmId(newFilm.getId());
        String sql = "Update films Set name = ?, description = ?, release_date = ?, duration = ? WHERE id =?";
        jdbc.update(sql, newFilm.getName(), newFilm.getDescription(), newFilm.getReleaseDate(), newFilm.getDuration(), newFilm.getId());
        newFilm.setGenres(loadGenres(newFilm));
        newFilm.setMpa(getMpaById(newFilm.getMpa().getId()));
        newFilm.setLikes(loadLikes(newFilm.getId()).stream().map(user -> user.getId()).collect(Collectors.toSet()));
        return newFilm;
    }

    @Override
    public Film getFilmById(Long id) {
        checkFilmId(id);
        String sql = "SELECT films.*, mpa.name as mpa_name FROM films" +
                " LEFT JOIN mpa ON films.mpa = mpa.id WHERE films.id = ?;";

        Film film = jdbc.queryForObject(sql, filmRowMapper, id);
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
        String sql = "DELETE FROM films WHERE id =?;";
        Film film = jdbc.queryForObject(sql,filmRowMapper,id);
        return film;
    }

    @Override
    public Film likeFilmById(Long filmId, Long userId) {
        checkFilmId(filmId);
        checkUserId(userId);
        String sql = "INSERT INTO films_likes (user_id, film_id) VALUES (?, ?);";
        jdbc.update(sql, userId, filmId);
        String sql1 = "SELECT films.*, mpa.name as mpa_name FROM films" +
                " LEFT JOIN mpa ON films.mpa = mpa.id WHERE films.id = ?;";
        Film film = jdbc.queryForObject(sql1, filmRowMapper, filmId);
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
        String sql = "DELETE FROM films_likes where user_id =? AND film_id = ?;";
        jdbc.update(sql, userId, filmId);
        String sql1 = "SELECT films.*, mpa.name as mpa_name FROM films" +
                " LEFT JOIN mpa ON films.mpa = mpa.id WHERE films.id = ?;";
        Film film = jdbc.queryForObject(sql1, filmRowMapper, filmId);
        film.setLikes(loadLikes(filmId)
                .stream()
                .map(User::getId)
                .collect(Collectors.toSet()));
        return film;
    }

    @Override
    public Collection<Film> getPopularFilms(Long count) {
        String sql ="SELECT films.*, mpa.name as mpa_name, COUNT(films_likes.user_id) as likes_count FROM films " +
                "LEFT JOIN films_likes ON films.id = films_likes.film_id " +
                "LEFT JOIN mpa ON films.mpa = mpa.id GROUP BY films.id, mpa.name " +
                "ORDER BY COUNT(films_likes.user_id) DESC LIMIT ?";
        List <Film> popularFilms = jdbc.query(sql, filmRowMapper, count);
        return popularFilms;
    }

    @Override
    public Collection<Genre> getGenres() {
        String sql = "SELECT * FROM genres;";
        List <Genre> genres = jdbc.query(sql, genreRowMapper);
        return genres;
    }

    @Override
    public Genre getGenresById(Long id) {
        checkGenreId(id);
        String sql = "Select * FROM genres WHERE id =?;";
        Genre genre  = jdbc.queryForObject(sql, genreRowMapper,id);
        return genre;
    }

    @Override
    public Collection<Mpa> getMpas() {
        String sql = "SELECT * FROM mpa;";
        List<Mpa> mpas = jdbc.query(sql,mpaRowMapper);
        return mpas;
    }

    @Override
    public Mpa getMpaById(Long id) {
        checkMpaId(id);
        String sql = "SELECT * FROM mpa WHERE id = ?;";
        Mpa mpa = jdbc.queryForObject(sql,mpaRowMapper, id);
        return mpa;
    }

    public void checkMpaId (Long id){
        String sql = "SELECT COUNT(*) FROM mpa where id=?;";
        if (jdbc.queryForObject(sql,Integer.class,id) == 0){
            throw new NotFoundException("Mpa with id " + id + " not found");
        };
    }

    public void checkFilmId(Long id){
        String sql = "SELECT COUNT(*) FROM films WHERE id = ?;";
        if (jdbc.queryForObject(sql,Integer.class,id) == 0){
            throw new NotFoundException("Film with id " + id + " not found");
        }
    }

    public void checkUserId(Long id){
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?;";
        if (jdbc.queryForObject(sql,Integer.class,id) == 0){
            throw new NotFoundException("User with id " + id + " not found");
        }
    }

    public void checkGenreId(Long id){
        String sql = "SELECT COUNT(*) FROM genres WHERE id = ?;";
        if (jdbc.queryForObject(sql,Integer.class,id) == 0 || jdbc.queryForObject(sql,Integer.class,id) == null){
            throw new NotFoundException("Genre with id " + id + " not found");
        }
    }

    public Set<Genre> loadGenres(Film film){
        String sql = "SELECT * FROM genres JOIN genres_films" +
                " ON genres.id = genres_films.genre_id WHERE film_id = ? ORDER BY genres.id ASC";
        List<Genre> genres = jdbc.query(sql, genreRowMapper, film.getId());
        Set<Genre> genres1 = new LinkedHashSet<>(genres);
        return genres1;
    }

    public Set<User> loadLikes(Long filmId){
        String sql = "SELECT users.* FROM films_likes JOIN users ON films_likes.user_id = users.id" +
                " WHERE films_likes.film_id = ?;";
        List <User> likes = jdbc.query(sql, userRowMapper, filmId);
        Set <User> likes1 = new LinkedHashSet<>(likes);
        return likes1;
    }
}
