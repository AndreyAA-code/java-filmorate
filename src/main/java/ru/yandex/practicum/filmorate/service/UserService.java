package ru.yandex.practicum.filmorate.service;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserService {

    //@Qualifier("InMemoryUserRepository")

    public final UserRepository userRepository;

   // @Autowired
   // public UserService(UserRepository userRepository) {
   //     this.userRepository = userRepository;
   // }


    public Collection<User> getAllUsers () {
        return userRepository.getAllUsers();
    }

    public User getUserById (Long id) {
       return userRepository.getUserById(id);
    }

    public User createUser(User user) {
        return userRepository.createUser(user);
    }

    public User updateUser(User user) {
        return userRepository.updateUser(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteUser(id);
    }

    public List<User> getUserFriends (Long id) {
       return userRepository.getUserFriends(id);
    }

    public List<User> updateUserFriends(Long id, Long friendId) {
        return userRepository.updateUserFriends(id, friendId);
    }

    public List<User> deleteUserFriends(Long id, Long friendId) {
        return userRepository.deleteUserFriends(id,friendId);
    }

    public Set<User> getCommonFriends(Long id, Long otherId) {
        return userRepository.getCommonFriends(id, otherId);
    }

}
