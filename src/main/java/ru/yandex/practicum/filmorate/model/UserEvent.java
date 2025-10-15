package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.dto.FilmDto;

import java.sql.Timestamp;

@Data
public class UserEvent {
    private Long eventId;
    private Long entityId;
    private Long userId;
    private EventType eventType;
    private Operation operation;
    private Timestamp timestamp;
}