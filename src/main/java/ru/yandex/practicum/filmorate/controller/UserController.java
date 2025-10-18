package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@RestController()
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@Valid @PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<UserDto> getUserFriends(@Valid @PathVariable Long id) {
        return userService.getUserFriends(id);
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public UserDto updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public List<UserDto> updateUserFriends(@Valid @PathVariable Long id, @Valid @PathVariable Long friendId) {
        return userService.updateUserFriends(id, friendId);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@Valid @RequestBody Long id) {
        userService.deleteUser(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public List<UserDto> deleteUserFriends(@Valid @PathVariable Long id, @Valid @PathVariable Long friendId) {
        return userService.deleteUserFriends(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<UserDto> getCommonFriends(@Valid @PathVariable Long id, @Valid @PathVariable Long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping ("/{id}/feed")
    public List<UserEvent> getUserFeeds(@Valid @PathVariable Long id) {
        return userService.getUserFeeds(id);
    }
}