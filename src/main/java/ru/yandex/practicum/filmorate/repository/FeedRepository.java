package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.UserEvent;

import java.util.List;

public interface FeedRepository {

    List<UserEvent> getFeedForUser(Long id);
}
