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
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.model.EventType.LIKE;
import static ru.yandex.practicum.filmorate.model.Operation.ADD;
import static ru.yandex.practicum.filmorate.model.Operation.REMOVE;

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
    private final DbFeedRepository dbFeedRepository;

    private static final String FIND_ALL_FILMS_QUERY = "SELECT films.*, mpa.name as mpa_name FROM films" +
            " LEFT JOIN mpa ON films.mpa_id = mpa.id ORDER BY films.id ASC;";
    private static final String UPDATE_FILM_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id =? WHERE id =?";
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

    private static final String BASE_SEARCH_SQL =
            "SELECT f.*, m.name AS mpa_name, COUNT(fl.film_id) AS likes_count " +
                    "FROM films f " +
                    "LEFT JOIN films_likes fl ON f.id = fl.film_id " +
                    "LEFT JOIN mpa m ON m.id = f.mpa_id " +
                    "LEFT JOIN directors_films df ON f.id = df.film_id " +
                    "LEFT JOIN directors d ON df.director_id = d.id ";

    private static final String LOAD_GENRES_BY_FILM_IDS_SQL_PREFIX =
            "SELECT gf.film_id, g.id, g.name " +
                    "FROM genres_films gf " +
                    "JOIN genres g ON g.id = gf.genre_id " +
                    "WHERE gf.film_id IN (";

    private static final String LOAD_DIRECTORS_BY_FILM_IDS_SQL_PREFIX =
            "SELECT df.film_id, d.id, d.name " +
                    "FROM directors_films df " +
                    "JOIN directors d ON d.id = df.director_id " +
                    "WHERE df.film_id IN (";

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
        jdbc.update(UPDATE_FILM_QUERY, newFilm.getName(), newFilm.getDescription(), newFilm.getReleaseDate(), newFilm.getDuration(), newFilm.getMpa().getId(), newFilm.getId());
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
        dbFeedRepository.createUserEvent(userId, filmId, LIKE, ADD);
        return film;
    }

    @Override
    public Film deleteLikeUser(Long filmId, Long userId) {
        checkFilmId(filmId);
        checkUserId(userId);
        dbFeedRepository.createUserEvent(userId, filmId, LIKE, REMOVE);
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

    @Override
    public Collection<Film> getFilmsBySearch(String query, String by) {
        if (query == null) query = "";
        if (by == null) by = "";
        String normBy = by.trim().toLowerCase();

        if (!normBy.equals("title") && !normBy.equals("director") && !normBy.equals("title,director")) {
            return List.of();
        }

        StringBuilder sql = new StringBuilder(BASE_SEARCH_SQL);

        List<Object> params = new ArrayList<>();
        if (normBy.equals("title")) {
            sql.append("WHERE LOWER(f.name) LIKE LOWER(?) ");
            params.add("%" + query + "%");
        } else if (normBy.equals("director")) {
            sql.append("WHERE LOWER(d.name) LIKE LOWER(?) ");
            params.add("%" + query + "%");
        } else {
            sql.append("WHERE LOWER(f.name) LIKE LOWER(?) OR LOWER(d.name) LIKE LOWER(?) ");
            params.add("%" + query + "%");
            params.add("%" + query + "%");
        }

        sql.append("GROUP BY f.id, m.id, m.name ")
                .append("ORDER BY likes_count DESC ");

        List<Film> films = jdbc.query(sql.toString(), params.toArray(), filmRowMapper);

        if (films.isEmpty()) return films;

        List<Long> ids = films.stream().map(Film::getId).collect(Collectors.toList());
        Map<Long, List<Genre>> genresByFilm = loadGenresByFilmIds(ids);
        Map<Long, List<Director>> directorsByFilm = loadDirectorsByFilmIds(ids);

        for (Film f : films) {
            List<Genre> gList = genresByFilm.get(f.getId());
            List<Director> dList = directorsByFilm.get(f.getId());

            f.setGenres(gList == null ? Collections.emptySet() : new LinkedHashSet<>(gList));
            f.setDirectors(dList == null ? Collections.emptySet() : new LinkedHashSet<>(dList));
        }

        return films;
    }

    private Map<Long, List<Genre>> loadGenresByFilmIds(List<Long> ids) {
        String inSql = ids.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = LOAD_GENRES_BY_FILM_IDS_SQL_PREFIX + inSql + ")";
        List<Object> params = new ArrayList<>(ids);
        return jdbc.query(sql, params.toArray(), rs -> {
            Map<Long, List<Genre>> map = new HashMap<>();
            while (rs.next()) {
                long filmId = rs.getLong("film_id");
                Genre g = new Genre(rs.getLong("id"), rs.getString("name"));
                map.computeIfAbsent(filmId, k -> new ArrayList<>()).add(g);
            }
            return map;
        });
    }

    private Map<Long, List<Director>> loadDirectorsByFilmIds(List<Long> ids) {
        String inSql = ids.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = LOAD_DIRECTORS_BY_FILM_IDS_SQL_PREFIX + inSql + ")";
        List<Object> params = new ArrayList<>(ids);
        return jdbc.query(sql, params.toArray(), rs -> {
            Map<Long, List<Director>> map = new HashMap<>();
            while (rs.next()) {
                long filmId = rs.getLong("film_id");
                Director d = new Director(rs.getLong("id"), rs.getString("name"));
                map.computeIfAbsent(filmId, k -> new ArrayList<>()).add(d);
            }
            return map;
        });
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