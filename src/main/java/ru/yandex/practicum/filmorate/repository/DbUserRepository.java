package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
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

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT * FROM users ORDER BY id ASC";
        List<User> users = jdbc.query(sql, mapper);
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
        String sql = "SELECT * FROM users WHERE id = ?";
        User user = jdbc.queryForObject(sql, mapper, id);

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
        String sql1 = "SELECT users.* FROM friends JOIN users ON friends.friend_id = users.id " +
                "WHERE friends.user_id = ?";
        List<User> friends = jdbc.query(sql1, mapper, id);
        return friends;
    }

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
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

        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id =?";
        jdbc.update(sql, newUser.getEmail(), newUser.getLogin(), newUser.getName(),
                newUser.getBirthday(), newUser.getId());

        return newUser;
    }

    @Override
    public void deleteUser(Long id) {
        checkUserId(id);
        String sql = "DELETE FROM users WHERE id = ?";
        jdbc.update(sql, id);
    }

    @Override
    public List<User> updateUserFriends(Long id, Long friendId) {
        checkUserId(id);
        checkUserId(friendId);

        String sql = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
        jdbc.update(sql, id, friendId);
        String sql1 = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbc.query(sql1, mapper, id);
        return users;
    }

    @Override
    public List<User> deleteUserFriends(Long id, Long friendId) {
        checkUserId(id);
        checkUserId(friendId);
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbc.update(sql, id, friendId);
        String sql1 = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbc.query(sql1, mapper, id);
        return users;
    }

    @Override
    public Set<User> getCommonFriends(Long id, Long otherId) {
        checkUserId(id);
        checkUserId(otherId);
        String sql = "SELECT users.* FROM friends AS f1 JOIN friends AS f2 \n" +
                "ON f1.friend_id = f2.friend_id JOIN users ON f1.friend_id = USERs.id " +
                "WHERE f1.user_Id = ? AND f2.user_Id = ?;";
        List <User> users = jdbc.query(sql, mapper, id, otherId);

        Set<User> commonFriends = new LinkedHashSet<>(users);

        return commonFriends;
    }

    public void checkUserId(Long userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        if (jdbc.queryForObject(sql, Integer.class, userId) == 0) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
    }


}
