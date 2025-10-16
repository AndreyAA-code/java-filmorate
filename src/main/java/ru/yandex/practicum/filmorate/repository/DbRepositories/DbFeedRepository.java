package ru.yandex.practicum.filmorate.repository.DbRepositories;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.repository.FeedRepository;

import java.util.List;

@Repository
@Primary
@AllArgsConstructor
public class DbFeedRepository implements FeedRepository {

    private final JdbcTemplate jdbcTemplate;
    private final FeedRowMapper feedRowMapper;

    private static final String GET_FEED_FOR_USER_ID = "SELECT * FROM user_events WHERE user_id = ?";


    @Override
    public List<UserEvent> getFeedForUser(Long id) {
        return jdbc.;
    }
}
