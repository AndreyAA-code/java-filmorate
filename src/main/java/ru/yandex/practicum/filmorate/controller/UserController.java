package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;

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
        return inMemoryUserStorage.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody final User user) {
        return inMemoryUserStorage.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody final User newUser) {
        return inMemoryUserStorage.update(newUser);
    }

}
