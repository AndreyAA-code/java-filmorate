package ru.yandex.practicum.filmorate.service;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.repository.FeedRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    public final UserRepository userRepository;
    public final FeedRepository feedRepository;

    public Collection<UserDto> getAllUsers() {
        return userRepository.getAllUsers()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        return UserMapper.mapToUserDto(userRepository.getUserById(id));
    }

    public UserDto createUser(User user) {
        return UserMapper.mapToUserDto(userRepository.createUser(user));
    }

    public UserDto updateUser(User user) {
        return UserMapper.mapToUserDto(userRepository.updateUser(user));
    }

    public void deleteUser(Long id) {
        userRepository.deleteUser(id);
    }

    public List<UserDto> getUserFriends(Long id) {
        return userRepository.getUserFriends(id)
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> updateUserFriends(Long id, Long friendId) {
        return userRepository.updateUserFriends(id, friendId)
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> deleteUserFriends(Long id, Long friendId) {
        return userRepository.deleteUserFriends(id, friendId)
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public Set<UserDto> getCommonFriends(Long id, Long otherId) {
        return userRepository.getCommonFriends(id, otherId)
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toSet());
    }

    public List<UserEvent> getUserFeeds(Long id) {
        userRepository.getUserById(id);
        return feedRepository.getFeedForUser(id);
    }
}