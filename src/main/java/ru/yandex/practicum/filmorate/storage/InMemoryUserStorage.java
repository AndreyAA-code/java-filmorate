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
        log.info("User: {} successfully got friend with id: {}", users.get(id),friendId);
        users.get(friendId).getFriends().remove(id);
        log.info("User: {} successfully got friend with id: {}", users.get(friendId),id);
    }

    @Override
    public List<User> getAllFriends(Long id) {
        getUserById(id);
        List<Long> friends_id = new ArrayList<>(users.get(id).getFriends());
        List<User> friends = new ArrayList<>();
        for (int index =0; index<friends_id.size();index++) {
            friends.add(users.get(friends_id.get(index)));
        }
        return friends;
    }

    @Override
    public Set<Long> getCommonFriends(Long id, Long otherId) {
        Set<Long> set1 = new HashSet<>(users.get(id).getFriends());
        set1.retainAll(users.get(otherId).getFriends());
        return set1;
    }

    @Override
    public User getUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("User not found with id: " + id);
        }
        return users.get(id);
    }
}
