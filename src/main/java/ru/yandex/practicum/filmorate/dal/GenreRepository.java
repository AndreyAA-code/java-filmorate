package ru.yandex.practicum.filmorate.dal;

import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;


@Repository
public class GenreRepository extends BaseRepository<Genre> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres Order By id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> getAllGenres() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Genre> getGenreById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }
}
