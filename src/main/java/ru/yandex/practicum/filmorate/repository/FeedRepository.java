package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.UserEvent;

import java.util.List;

public interface FeedRepository {

    List<UserEvent> getFeedForUser(Long id);

    void createUserEvent(Long id, Long friendId, EventType eventType, Operation operation);
}
