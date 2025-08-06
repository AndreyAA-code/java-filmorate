package ru.yandex.practicum.filmorate.storage;

import jakarta.validation.Valid;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage {

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @Getter
    public final Map<Long, User> users = new HashMap<>();

    public Map<Long, User> getUsers() {
        return users;
    }
   // @Override
    public Collection<User> findAll() {
        log.debug("Find all users");
        return users.values();
    }

   // @Override
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

 //   @Override
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
        throw new ValidationException("Такого Id нет");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
        log.debug("New id: {} succesfully generated", currentMaxId + 1);
        return ++currentMaxId;
    }
}
