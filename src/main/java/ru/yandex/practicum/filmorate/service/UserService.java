package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@RequiredArgsConstructor
@Service
public class UserService {

    final UserStorage userStorage = new InMemoryUserStorage();

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    public void addFriend(Long id, Long friendId) {
        userStorage.addFriend(id, friendId);
    }

    public void removeFriend(Long id, Long friendId) {
        userStorage.removeFriend(id, friendId);
    }

    public Set<Long> getAllFriends(Long id) {
       return userStorage.getAllFriends(id);
    }

    public Set<Long> getCommonFriends(Long id, Long otherId) {
       return userStorage.getCommonFriends(id, otherId);
    }

}
