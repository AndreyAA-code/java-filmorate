package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    Collection<User> findAll();

    User create(User user);

    User update(User newUser);

    long getNextId();

    List<User> addFriend(Long id, Long friendId);

    void removeFriend(Long id, Long friendId);

    List<User> getAllFriends(Long id);

    List<User> getCommonFriends(Long id, Long otherId);

    User getUserById(Long id);
}
