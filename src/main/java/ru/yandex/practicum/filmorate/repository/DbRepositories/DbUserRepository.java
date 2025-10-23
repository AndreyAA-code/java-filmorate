package ru.yandex.practicum.filmorate.repository.DbRepositories;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.repository.mappers.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.model.EventType.FRIEND;
import static ru.yandex.practicum.filmorate.model.Operation.ADD;
import static ru.yandex.practicum.filmorate.model.Operation.REMOVE;

@Slf4j
@Repository
@AllArgsConstructor
@Primary
public class DbUserRepository implements UserRepository {
    private static final String FIND_ALL_USERS_QUERY = "SELECT * FROM users ORDER BY id ASC";
    private static final String FIND_USERS_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_USER_FRIENDS_QUERY = "SELECT users.* FROM friends JOIN users " +
            "ON friends.friend_id = users.id WHERE friends.user_id = ?";
    private static final String CREATE_USER_QUERY = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id =?";

    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String DELETE_FILM_LIKES_QUERY = "DELETE FROM films_likes WHERE user_id = ?";
    private static final String DELETE_REVIEWS_USERS_QUERY = "DELETE FROM reviews_users WHERE user_id = ?";
    private static final String DELETE_REVIEWS_QUERY = "DELETE FROM reviews WHERE user_id = ?";
    private static final String DELETE_FRIENDS_USER_ID_QUERY = "DELETE FROM friends WHERE user_id = ?";
    private static final String DELETE_FRIENDS_FRIEND_ID_QUERY = "DELETE FROM friends WHERE friend_id = ?";

    private static final String ADD_USER_FRIEND_QUERY = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
    private static final String DELETE_USER_FRIEND_QUERY = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String FIND_COMMON_FRIENDS_QUERY = "SELECT users.* FROM friends AS f1 JOIN friends AS f2 \n" +
            "ON f1.friend_id = f2.friend_id JOIN users ON f1.friend_id = USERs.id " +
            "WHERE f1.user_Id = ? AND f2.user_Id = ?;";
    private static final String IF_USER_EXISTS_QUERY = "SELECT COUNT(*) FROM users WHERE id = ?";
    private final UserRowMapper mapper;
    private final DbFeedRepository dbFeedRepository;
    private final FilmRepository filmRepository;
    private JdbcTemplate jdbc;

    @Override
    public Collection<User> getAllUsers() {
        log.info("Get all users");
        List<User> users = jdbc.query(FIND_ALL_USERS_QUERY, mapper);
        for (User user : users) {
            Set<Long> friendIds = getUserFriends(user.getId())
                    .stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());
            user.setFriends(new LinkedHashSet<>(friendIds));
        }
        return users;
    }

    @Override
    public User getUserById(Long id) {
        log.info("Get user by id {}", id);
        checkUserId(id);
        User user = jdbc.queryForObject(FIND_USERS_BY_ID_QUERY, mapper, id);

        Set<Long> friendIds = getUserFriends(id)
                .stream()
                .map(User::getId)
                .collect(Collectors.toSet());
        user.setFriends(new LinkedHashSet<>(friendIds));
        log.info("Get friends by id {}", friendIds);
        return user;
    }

    @Override
    public List<User> getUserFriends(Long id) {
        log.info("Get friends by id {}", id);
        checkUserId(id);
        List<User> friends = jdbc.query(FIND_USER_FRIENDS_QUERY, mapper, id);
        log.info("Get friends by id {}", friends);
        return friends;
    }

    @Override
    public User createUser(User user) {
        log.info("Create user {}", user);
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_USER_QUERY, new String[]{"id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        Long generatedId = keyHolder.getKey().longValue();
        log.info("Generated id {}", generatedId);
        user.setId(generatedId);
        log.info("Created user {}", user);
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        log.info("Update user {}", newUser);
        checkUserId(newUser.getId());
        jdbc.update(UPDATE_USER_QUERY, newUser.getEmail(), newUser.getLogin(), newUser.getName(),
                newUser.getBirthday(), newUser.getId());
        log.info("Updated user {}", newUser);
        return newUser;
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Delete user {}", id);
        checkUserId(id);

        // Удаление зависимостей
        jdbc.update(DELETE_FILM_LIKES_QUERY, id);
        jdbc.update(DELETE_REVIEWS_USERS_QUERY, id);
        jdbc.update(DELETE_REVIEWS_QUERY, id);
        jdbc.update(DELETE_FRIENDS_USER_ID_QUERY, id);
        jdbc.update(DELETE_FRIENDS_FRIEND_ID_QUERY, id);

        jdbc.update(DELETE_USER_QUERY, id);
        log.info("Deleted user {}", id);
    }

    @Override
    public List<User> updateUserFriends(Long id, Long friendId) {
        log.info("Update user friends by id {}", friendId);
        checkUserId(id);
        checkUserId(friendId);

        jdbc.update(ADD_USER_FRIEND_QUERY, id, friendId);
        dbFeedRepository.createUserEvent(id, friendId, FRIEND, ADD);
        List<User> users = jdbc.query(FIND_USERS_BY_ID_QUERY, mapper, id);
        return users;
    }

    @Override
    public List<User> deleteUserFriends(Long id, Long friendId) {
        log.info("Delete user friends by id {}", friendId);
        checkUserId(id);
        checkUserId(friendId);

        jdbc.update(DELETE_USER_FRIEND_QUERY, id, friendId);
        dbFeedRepository.createUserEvent(id, friendId, FRIEND, REMOVE);
        List<User> users = jdbc.query(FIND_USER_FRIENDS_QUERY, mapper, id);
        return users;
    }

    @Override
    public Set<User> getCommonFriends(Long id, Long otherId) {
        log.info("Get common friends by id {}", otherId);
        checkUserId(id);
        checkUserId(otherId);
        List<User> users = jdbc.query(FIND_COMMON_FRIENDS_QUERY, mapper, id, otherId);
        Set<User> commonFriends = new LinkedHashSet<>(users);
        log.info("Get common friends by id {}", commonFriends);
        return commonFriends;
    }

    @Override
    public List<Film> getFilmsRecommendations(Long id) {
        log.info("Get recommendations by id {}", id);
        checkUserId(id);
        return filmRepository.getFilmsRecommendations(id);
    }

    @Override
    public void checkUserId(Long userId) {
    log.info("Check user id {}", userId);
        if (jdbc.queryForObject(IF_USER_EXISTS_QUERY, Integer.class, userId) == 0) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
    }

}