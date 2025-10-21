package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserRepository {

    Collection<User> getAllUsers();

    User getUserById(Long id);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);

    List<User> getUserFriends(Long id);

    List<User> updateUserFriends(Long id, Long friendId);

    List<User> deleteUserFriends(Long id, Long friendId);

    Set<User> getCommonFriends(Long id, Long otherId);

    void checkUserId(Long userId);
}