package ru.yandex.practicum.filmorate.storage;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {

    Collection<User> findAll();

    User create(User user);

    User update(@Valid @RequestBody User newUser);

    long getNextId();

    Set<Long> addFriend(Long id, Long friendId);

    void removeFriend(Long id, Long friendId);

    Set<Long> getAllFriends(Long id);

    Set<Long> getCommonFriends(Long id, Long otherId);

    boolean getUserById(Long id);
}
