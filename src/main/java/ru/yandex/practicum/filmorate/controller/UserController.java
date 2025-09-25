package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@RestController()
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    public final UserService userService;

    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@Valid @PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@Valid @PathVariable Long id) {
        return userService.getUserFriends(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public List<User> updateUserFriends(@Valid @PathVariable Long id, @Valid @PathVariable Long friendId) {
        return userService.updateUserFriends(id, friendId);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@Valid @RequestBody Long id) {
        userService.deleteUser(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public List<User> deleteUserFriends(@Valid @PathVariable Long id, @Valid @PathVariable Long friendId) {
        return userService.deleteUserFriends(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(@Valid @PathVariable Long id, @Valid @PathVariable Long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

}
