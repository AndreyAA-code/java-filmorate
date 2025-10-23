package ru.yandex.practicum.filmorate.repository.DbRepositories;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.MpaRepository;
import ru.yandex.practicum.filmorate.repository.mappers.MpaRowMapper;

import java.util.Collection;
import java.util.List;

@Slf4j
@Repository
@AllArgsConstructor
@Primary
public class DbMpaRepository implements MpaRepository {

    private final JdbcTemplate jdbc;
    private final MpaRowMapper mpaRowMapper;

    private static final String GET_ALL_MPA_QUERY = "SELECT * FROM mpa ORDER BY id ASC;";
    private static final String GET_MPA_BY_ID_QUERY = "SELECT * FROM mpa WHERE id = ?;";
    private static final String IF_MPA_EXISTS_QUERY = "SELECT COUNT(*) FROM mpa where id=?;";

    @Override
    public Collection<Mpa> getMpas() {
        log.info("Get mpas");
        List<Mpa> mpas = jdbc.query(GET_ALL_MPA_QUERY, mpaRowMapper);
        return mpas;
    }

    @Override
    public Mpa getMpaById(Long id) {
        log.info("Get mpa by id: {}", id);
        checkMpaId(id);
        Mpa mpa = jdbc.queryForObject(GET_MPA_BY_ID_QUERY, mpaRowMapper, id);
        return mpa;
    }

    @Override
    public void checkMpaId(Long id) {
        log.info("Check mpa by id: {}", id);
        if (jdbc.queryForObject(IF_MPA_EXISTS_QUERY, Integer.class, id) == 0) {
            throw new NotFoundException("Mpa with id " + id + " not found");
        }
    }
}
