package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Primary
@RequiredArgsConstructor
@Component

public class InMemoryUserStorage implements UserStorage {

    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        log.debug("Find all users");
        return users.values();
    }

    @Override
    public User create(User user) {
        log.info("Create user: {}", user);
        log.debug("User: {} send to validation", user);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        log.debug("User: {} successfully validated", user);
        user.setId(getNextId());
        log.debug("User: {} created with id: {}", user, user.getId());
        users.put(user.getId(), user);
        log.info("User: {} successfully created with id: {}", user, user.getId());
        return user;
    }

    @Override
    public User update(User newUser) {
        if (newUser.getId() == null) {
            log.debug("Update user: {} started", newUser);
            throw new ValidationException("id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            log.debug("User: {} with Id: {} found in the database", newUser, newUser.getId());
            User oldUser = users.get(newUser.getId());
            log.debug("User: {} send to validation", newUser);
            log.debug("User: {} successfully validated", newUser);
            oldUser.setName(newUser.getName());
            oldUser.setEmail(newUser.getEmail());
            oldUser.setBirthday(newUser.getBirthday());
            oldUser.setLogin(newUser.getLogin());
            log.debug("User: {} info successfully updated", oldUser);
            return oldUser;
        }
        throw new NotFoundException("Такого Id нет");
    }

    @Override
    public long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
        log.debug("New id: {} succesfully generated", currentMaxId + 1);
        return ++currentMaxId;
    }


    @Override
    public List<User> addFriend(Long id, Long friendId) {
        getUserById(id);
        getUserById(friendId);
        users.get(id).getFriends().add(friendId);
        log.debug("User: {} added friend: {}", id, friendId);
        users.get(friendId).getFriends().add(id);
        List<User> friends = List.of(users.get(id), users.get(friendId));
        List<Long> testFriend = new ArrayList<>(users.get(id).getFriends());
        return friends;
    }

    @Override
    public void removeFriend(Long id, Long friendId) {
        getUserById(id);
        getUserById(friendId);
        users.get(id).getFriends().remove(friendId);
        log.info("User: {} successfully got friend with id: {}", users.get(id), friendId);
        users.get(friendId).getFriends().remove(id);
        log.info("User: {} successfully got friend with id: {}", users.get(friendId), id);
    }

    @Override
    public List<User> getAllFriends(Long id) {
        getUserById(id);
        List<Long> friendsId = new ArrayList<>(users.get(id).getFriends());
        List<User> friends = new ArrayList<>();
        for (int index = 0; index < friendsId.size(); index++) {
            friends.add(users.get(friendsId.get(index)));
        }
        return friends;
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        List<Long> user1friendsId = new ArrayList<>(users.get(id).getFriends());
        System.out.println("user1friendsId" + user1friendsId);
        List<Long> user2friendsId = new ArrayList<>(users.get(otherId).getFriends());
        System.out.println("user2friendsId" + user2friendsId);
        user1friendsId.retainAll(user2friendsId);
        System.out.println("user1friends_id: " + user1friendsId);
        List<User> commonFriends = new ArrayList<>();
        System.out.println("commonFriends: " + commonFriends);
        for (int index = 0; index < user1friendsId.size(); index++) {
            commonFriends.add(users.get(user1friendsId.get(index)));
        }
        System.out.println("commonFriends: " + commonFriends);
        return commonFriends;

    }

    @Override
    public User getUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("User not found with id: " + id);
        }
        return users.get(id);
    }
}
