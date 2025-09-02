package ru.yandex.practicum.filmorate.dal;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class BaseRepository<T> {

    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;


    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbc.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected List<T> findMany(String query, Object... params) {
        return jdbc.query(query, mapper, params);
    }

    protected boolean delete(String query, long id) {
        int rowsDeleted = jdbc.update(query, id);
        return rowsDeleted > 0;
    }

    protected void update(String query, Object... params) {
       try {
           log.debug("Executing UPDATE query: {}", query);
           log.debug("Query parameters: {}", Arrays.toString(params));
        int rowsUpdated = jdbc.update(query, params);
           log.debug("Rows updated: {}", rowsUpdated);

        if (rowsUpdated == 0) {
            log.error("No rows were updated for query: {}", query);
            throw new InternalServerException("Не удалось обновить данные");
        }
        } catch (EmptyResultDataAccessException ignored) {
               log.error("Database error during update: ");
               throw new InternalServerException("Database error:");
           }
        }


    protected long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
                return ps;} , keyHolder);
        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            return id;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }
}
