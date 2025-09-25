package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;


@Repository
@Slf4j

public class InMemoryUserRepository implements UserRepository {

    Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("User with id " + id + " not found");
        }
        return users.get(id);
    }

    @Override
    public User createUser(User user) {
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        checkUser(newUser);
        User oldUser = users.get(newUser.getId());
        if (newUser.getName() == null || newUser.getName().isEmpty()) {
            newUser.setName(newUser.getLogin());
        }
        oldUser.setLogin(newUser.getLogin());
        oldUser.setName(newUser.getName());
        oldUser.setEmail(newUser.getEmail());
        oldUser.setBirthday(newUser.getBirthday());
        return oldUser;
    }

    @Override
    public void deleteUser(Long id) {
        checkUser(users.get(id));
        users.remove(id);
    }

    public void checkUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("User with id " + user.getId() + " not found");
        }
    }

    public void checkUserId(Long id) {
        if (users.get(id) == null){
            throw new NotFoundException("User with id " + id + " not found");
        }
    }

    @Override
    public List<User> getUserFriends(Long id) {
        checkUserId(id);
        return users.get(id).getFriends()
                .stream()
                .map(userId -> users.get(userId))
                .collect(Collectors.toList());
    }

    public Long getNextId() {
        Long nextId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
        return ++nextId;
    }

    @Override
    public List<User> updateUserFriends(Long id, Long friendId) {
        checkUserId(id);
        checkUserId(friendId);
        users.get(id).getFriends().add(friendId);
        //users.get(friendId).getFriends().add(id); //изменилось правило в ФЗ 12
        return users.get(id).getFriends()
                .stream()
                .map(userId->users.get(userId))
                .collect(Collectors.toList());
    }

@Override
    public List<User> deleteUserFriends(Long id, Long friendId) {
        checkUserId(id);
        checkUserId(friendId);
        users.get(id).getFriends().remove(friendId);
      //  users.get(friendId).getFriends().remove(id); //изменилось правило в ФЗ 12
        return users.get(id).getFriends()
                .stream()
                .map(userId->users.get(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Set<User> getCommonFriends(Long id, Long otherId) {
        checkUserId(id);
        checkUserId(otherId);
        Set<User> friends1 = users.get(id).getFriends()
                .stream()
                .map(userId->users.get(userId))
                .collect(Collectors.toSet());
        Set<User> friends2 =users.get(otherId).getFriends()
                .stream()
                .map(userId->users.get(userId))
                .collect(Collectors.toSet());
        friends1.retainAll(friends2);
        return friends1;
    }
}
