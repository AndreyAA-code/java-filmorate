package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final Map<Long, Set> friends = new HashMap<>();

    public void addFriend(Long id, Long friendId) {
        InMemoryUserStorage.users.get(id).getFriends().add(friendId);
    }

    public void removeFriend(Long id, Long friendId) {
        InMemoryUserStorage.users.get(id).getFriends().remove(friendId);
    }


}
