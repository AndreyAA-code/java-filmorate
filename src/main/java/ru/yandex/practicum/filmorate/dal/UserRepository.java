package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String INSERT_QUERY = "INSERT INTO users (name,email,login,birthday)" + " VALUES (?,?,?,?) returning id";
    private static final String UPDATE_QUERY = "UPDATE users SET name=?,email=?,login=?,birthday=? WHERE id=?";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE id=?";

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public List<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<User> findByEmail(String email) {
        return findOne(FIND_BY_EMAIL_QUERY, email);
    }

    public Optional<User> findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public User save(User user) {
        long id = insert(INSERT_QUERY, user.getName(), user.getEmail(), user.getBirthday(), user.getLogin());
        user.setId(id);
        return user;
    }

    public User update(User user) {
        update(UPDATE_QUERY, user.getId(), user.getName(), user.getEmail(), user.getBirthday(), user.getLogin(), user.getId());
        return user;
    }

}
