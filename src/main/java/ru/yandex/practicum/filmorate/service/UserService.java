package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;

@RequiredArgsConstructor
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    InMemoryUserStorage userStorage = new InMemoryUserStorage();

    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    public User addFriend(Long id, Long friendId) {
        userStorage.users.get(id).getFriends().add(friendId);
        userStorage.users.get(friendId).getFriends().add(id);
        return userStorage.users.get(id);
    }

    public void removeFriend(Long id, Long friendId) {
        userStorage.users.get(id).getFriends().remove(friendId);
        log.info("User: {} successfully got friend with id: {}", userStorage.users.get(id),friendId);
        userStorage.users.get(friendId).getFriends().remove(id);
        log.info("User: {} successfully got friend with id: {}", userStorage.users.get(friendId),id);
    }

    public User getAllFriends(Long id) {
        userStorage.users.get(id).getFriends();
        return userStorage.users.get(id);
    }

    public Set<Long> getCommonFriends(Long id, Long otherId) {
        Set<Long> set1 = new HashSet<>(userStorage.users.get(id).getFriends());
        set1.retainAll(userStorage.users.get(otherId).getFriends());
        System.out.println(set1);
        return set1;
    }

}
