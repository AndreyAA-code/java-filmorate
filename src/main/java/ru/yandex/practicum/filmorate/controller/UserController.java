package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserController(final InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @GetMapping
    public Collection<User> findAll() {
        return InMemoryUserStorage.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody final User user) {
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

    @PutMapping
    public User update(@Valid @RequestBody final User newUser) {
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
