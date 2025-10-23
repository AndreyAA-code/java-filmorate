package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.repository.FeedRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    public final UserRepository userRepository;
    public final FeedRepository feedRepository;


    public Collection<UserDto> getAllUsers() {
        log.info("Get all users");
        return userRepository.getAllUsers()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        log.info("Get user by id: {}", id);
        return UserMapper.mapToUserDto(userRepository.getUserById(id));
    }

    public UserDto createUser(User user) {
        log.info("Create user: {}", user);
        return UserMapper.mapToUserDto(userRepository.createUser(user));
    }

    public UserDto updateUser(User user) {
        log.info("Update user: {}", user);
        return UserMapper.mapToUserDto(userRepository.updateUser(user));
    }

    public void deleteUser(Long id) {
        log.info("Delete user: {}", id);
        userRepository.deleteUser(id);
    }

    public List<UserDto> getUserFriends(Long id) {
        log.info("Get user friends by id: {}", id);
        return userRepository.getUserFriends(id)
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> updateUserFriends(Long id, Long friendId) {
        log.info("Update user friends by id: {}", id);
        return userRepository.updateUserFriends(id, friendId)
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> deleteUserFriends(Long id, Long friendId) {
        log.info("Delete user friends by id: {}", id);
        return userRepository.deleteUserFriends(id, friendId)
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public Set<UserDto> getCommonFriends(Long id, Long otherId) {
        log.info("Get common friends by id: {}", id);
        return userRepository.getCommonFriends(id, otherId)
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toSet());
    }

    public List<UserEvent> getUserFeeds(Long id) {
        log.info("Get user feeds by id: {}", id);
        userRepository.getUserById(id);
        return feedRepository.getFeedForUser(id);
    }

    public List<FilmDto> getFilmsRecommendations(@Valid Long id) {
        log.info("Get film recommendations by id: {}", id);
        return userRepository.getFilmsRecommendations(id)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }
}