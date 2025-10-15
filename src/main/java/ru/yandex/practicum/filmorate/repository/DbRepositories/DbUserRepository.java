package ru.yandex.practicum.filmorate.repository.DbRepositories;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.repository.mappers.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
@Primary
public class DbUserRepository implements UserRepository {
    private JdbcTemplate jdbc;
    private final UserRowMapper mapper;

    private static final String FIND_ALL_USERS_QUERY = "SELECT * FROM users ORDER BY id ASC";
    private static final String FIND_USERS_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_USER_FRIENDS_QUERY = "SELECT users.* FROM friends JOIN users " +
            "ON friends.friend_id = users.id WHERE friends.user_id = ?";
    private static final String CREATE_USER_QUERY = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id =?";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String ADD_USER_FRIEND_QUERY = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
    private static final String DELETE_USER_FRIEND_QUERY = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String FIND_COMMON_FRIENDS_QUERY = "SELECT users.* FROM friends AS f1 JOIN friends AS f2 \n" +
            "ON f1.friend_id = f2.friend_id JOIN users ON f1.friend_id = USERs.id " +
            "WHERE f1.user_Id = ? AND f2.user_Id = ?;";
    private static final String IF_USER_EXISTS_QUERY = "SELECT COUNT(*) FROM users WHERE id = ?";

    @Override
    public Collection<User> getAllUsers() {
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
        checkUserId(id);
        User user = jdbc.queryForObject(FIND_USERS_BY_ID_QUERY, mapper, id);

        Set<Long> friendIds = getUserFriends(id)
                .stream()
                .map(User::getId)
                .collect(Collectors.toSet());
        user.setFriends(new LinkedHashSet<>(friendIds));
        return user;
    }

    @Override
    public List<User> getUserFriends(Long id) {
        checkUserId(id);
        List<User> friends = jdbc.query(FIND_USER_FRIENDS_QUERY, mapper, id);
        return friends;
    }

    @Override
    public User createUser(User user) {
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
        user.setId(generatedId);
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        checkUserId(newUser.getId());

        jdbc.update(UPDATE_USER_QUERY, newUser.getEmail(), newUser.getLogin(), newUser.getName(),
                newUser.getBirthday(), newUser.getId());

        return newUser;
    }

    @Override
    public void deleteUser(Long id) {
        checkUserId(id);
        jdbc.update(DELETE_USER_QUERY, id);
    }

    @Override
    public List<User> updateUserFriends(Long id, Long friendId) {
        checkUserId(id);
        checkUserId(friendId);

        jdbc.update(ADD_USER_FRIEND_QUERY, id, friendId);
        List<User> users = jdbc.query(FIND_USERS_BY_ID_QUERY, mapper, id);
        return users;
    }

    @Override
    public List<User> deleteUserFriends(Long id, Long friendId) {
        checkUserId(id);
        checkUserId(friendId);

        jdbc.update(DELETE_USER_FRIEND_QUERY, id, friendId);
        List<User> users = jdbc.query(FIND_USER_FRIENDS_QUERY, mapper, id);
        return users;
    }

    @Override
    public Set<User> getCommonFriends(Long id, Long otherId) {
        checkUserId(id);
        checkUserId(otherId);

        List<User> users = jdbc.query(FIND_COMMON_FRIENDS_QUERY, mapper, id, otherId);

        Set<User> commonFriends = new LinkedHashSet<>(users);

        return commonFriends;
    }

    public void checkUserId(Long userId) {

        if (jdbc.queryForObject(IF_USER_EXISTS_QUERY, Integer.class, userId) == 0) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
    }

}