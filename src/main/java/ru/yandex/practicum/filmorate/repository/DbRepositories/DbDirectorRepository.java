package ru.yandex.practicum.filmorate.repository.DbRepositories;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.DirectorRepository;
import ru.yandex.practicum.filmorate.repository.mappers.DirectorRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
@AllArgsConstructor
@Primary
public class DbDirectorRepository implements DirectorRepository {

    private final JdbcTemplate jdbc;
    private final DirectorRowMapper directorRowMapper;

    private static final String FIND_ALL_DIRECTORS_QUERY = "SELECT * FROM directors ORDER BY id";
    private static final String FIND_DIRECTOR_BY_ID_QUERY = "SELECT * FROM directors WHERE id = ?";
    private static final String ADD_DIRECTOR_QUERY = "INSERT INTO directors(name) VALUES (?)";
    private static final String UPDATE_DIRECTOR_QUERY = "UPDATE directors SET name = ? WHERE id = ?";
    private static final String DELETE_DIRECTOR_QUERY = "DELETE FROM directors WHERE id = ?";
    private static final String IF_DIRECTOR_EXISTS_QUERY = "SELECT COUNT(*) FROM directors WHERE id = ?";
    private static final String GET_DIRECTORS_FOR_FILM_QUERY = "SELECT * FROM directors d JOIN directors_films df " +
            "ON d.id = df.director_id WHERE df.film_id = ? ORDER BY d.id ASC";

    @Override
    public Collection<Director> getDirectors() {
        return jdbc.query(FIND_ALL_DIRECTORS_QUERY, directorRowMapper);
    }

    @Override
    public Director getDirectorById(Long id) {
        checkDirectorId(id);
        return jdbc.queryForObject(FIND_DIRECTOR_BY_ID_QUERY, directorRowMapper, id);
    }

    @Override
    public Director createDirector(Director director) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(ADD_DIRECTOR_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        checkDirectorId(director.getId());
        jdbc.update(UPDATE_DIRECTOR_QUERY, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirector(Long id) {
        checkDirectorId(id);
        jdbc.update(DELETE_DIRECTOR_QUERY, id);
    }

    public void checkDirectorId(Long id) {
        if (jdbc.queryForObject(IF_DIRECTOR_EXISTS_QUERY, Integer.class, id) == 0) {
            throw new NotFoundException("Director with id " + id + " not found");
        }
    }

    public Set<Director> loadDirectors(Long filmId) {
        List<Director> directors = jdbc.query(GET_DIRECTORS_FOR_FILM_QUERY, directorRowMapper, filmId);
        return new LinkedHashSet<>(directors);
    }

}
