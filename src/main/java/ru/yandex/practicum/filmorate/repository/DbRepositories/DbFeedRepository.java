package ru.yandex.practicum.filmorate.repository.DbRepositories;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.repository.FeedRepository;
import ru.yandex.practicum.filmorate.repository.mappers.UserEventRowMapper;

import java.sql.PreparedStatement;
import java.util.List;

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
        return jdbc.query(GET_FEED_FOR_USER_ID_QUERY, userEventRowMapper, id);
    }

    public void createUserEvent(Long userId, Long entityId, String eventType, String operation) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Long timestamp = System.currentTimeMillis();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(ADD_USER_EVENT_QUERY, new String[]{"eventId"});
            ps.setLong(1, userId);
            ps.setLong(2, entityId);
            ps.setString(3, eventType);
            ps.setString(4, operation);
            ps.setLong(5, timestamp);
            return ps;
        }, keyHolder);
        Long generatedId = keyHolder.getKey().longValue();
      //  userEvent.setEventId(generatedId);
       // return userEvent;
    }

}
