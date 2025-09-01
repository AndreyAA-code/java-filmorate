package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mapper.UserMapper;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }
}



    /*private final UserStorage userStorage;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    public List<User> addFriend(Long id, Long friendId) {
        return userStorage.addFriend(id, friendId);
    }

    public void removeFriend(Long id, Long friendId) {
        userStorage.removeFriend(id, friendId);
    }

    public List<User> getAllFriends(Long id) {
        return userStorage.getAllFriends(id);
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        return userStorage.getCommonFriends(id, otherId);
    }

}
*/