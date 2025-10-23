package ru.yandex.practicum.filmorate.repository.DbRepositories;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.repository.FeedRepository;
import ru.yandex.practicum.filmorate.repository.mappers.UserEventRowMapper;

import java.sql.PreparedStatement;
import java.util.List;

@Slf4j
@Repository
@Primary
@AllArgsConstructor
public class DbFeedRepository implements FeedRepository {

    private final JdbcTemplate jdbc;
    private final UserEventRowMapper userEventRowMapper;
    private static final String GET_FEED_FOR_USER_ID_QUERY = "SELECT * FROM user_events WHERE userId = ?";
    private static final String ADD_USER_EVENT_QUERY = "INSERT INTO user_events (userId, entityId, eventType, operation, timestamp) VALUES (?, ?, ?, ?, ?)";

    @Override
    public List<UserEvent> getFeedForUser(Long id) {
        log.info("Get feed for user: {}", id);
        return jdbc.query(GET_FEED_FOR_USER_ID_QUERY, userEventRowMapper, id);
    }

    @Override
    public void createUserEvent(Long userId, Long entityId, EventType eventType, Operation operation) {
        log.info("Create user event for user: {}", userId);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Long timestamp = System.currentTimeMillis();
        log.trace("Created timestamp {} for user event", timestamp);
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(ADD_USER_EVENT_QUERY, new String[]{"eventId"});
            ps.setLong(1, userId);
            ps.setLong(2, entityId);
            ps.setString(3, String.valueOf(eventType));
            ps.setString(4, String.valueOf(operation));
            ps.setLong(5, timestamp);
            return ps;
        }, keyHolder);
    }
}
