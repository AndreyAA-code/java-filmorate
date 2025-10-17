package ru.yandex.practicum.filmorate.repository.DbRepositories;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.DirectorRepository;
import ru.yandex.practicum.filmorate.repository.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.FilmRowMapper;

import java.util.Collection;
import java.util.List;

@Repository
@AllArgsConstructor
@Primary
public class DbDirectorRepository implements DirectorRepository {

    private final JdbcTemplate jdbc;
    private final DirectorRowMapper directorRowMapper;
    private final FilmRowMapper filmRowMapper;

    @Override
    public Collection<Director> getDirectors() {
        return List.of();
    }

    @Override
    public Director getDirectorById(Long id) {
        return null;
    }

    @Override
    public Director createDirector(Director director) {
        return null;
    }

    @Override
    public Director updateDirector(Director director) {
        return null;
    }

    @Override
    public void deleteDirector(Long id) {

    }

    @Override
    public Collection<Film> getFilmsByDirector(Long directorId, String sortBy) {
        return List.of();
    }
}
