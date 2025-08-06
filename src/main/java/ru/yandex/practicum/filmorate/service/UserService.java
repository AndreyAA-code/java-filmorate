package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final Map<Long, Set> friends = new HashMap<>();
    private UserStorage userStorage;

    public User addFriend(Long id, Long friendId) {
        InMemoryUserStorage.users.get(id).getFriends().add(friendId);
        //InMemoryUserStorage.users.get(friendId).getFriends().add(id);
        return InMemoryUserStorage.users.get(id);
    }

    public void removeFriend(Long id, Long friendId) {
        InMemoryUserStorage.users.get(id).getFriends().remove(friendId);
        InMemoryUserStorage.users.get(friendId).getFriends().remove(id);
    }

    public User getAllFriends(Long id){
        InMemoryUserStorage.users.get(id).getFriends();
        return InMemoryUserStorage.users.get(id);
    }

    public boolean getCommonFriends(Long id, Long otherId) {
        Set<Long> set1 = new HashSet<>(InMemoryUserStorage.users.get(id).getFriends());
        return set1.retainAll(InMemoryUserStorage.users.get(otherId).getFriends());
    }

}
