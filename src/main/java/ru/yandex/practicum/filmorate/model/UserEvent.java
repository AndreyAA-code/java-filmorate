package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class UserEvent {
    private Long eventId;
    private Long entityId;
    private Long userId;
    private EventType eventType;
    private Operation operation;
    private Long timestamp;
}