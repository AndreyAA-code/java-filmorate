package ru.yandex.practicum.filmorate.repository.mappers;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.UserEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

@AllArgsConstructor
@Component
public class UserEventRowMapper implements RowMapper<UserEvent> {

    @Override
    public UserEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserEvent userEvent = new UserEvent();
        userEvent.setEventId(rs.getLong("eventId"));
        userEvent.setUserId(rs.getLong("userId"));
        userEvent.setEntityId(rs.getLong("entityId"));
        EventType eventType = EventType.valueOf(rs.getString("eventType"));
        userEvent.setEventType(eventType);
        Operation operation = Operation.valueOf(rs.getString("operation"));
        userEvent.setOperation(operation);
        userEvent.setTimestamp(rs.getLong("timestamp"));
        return userEvent;
    }
}
