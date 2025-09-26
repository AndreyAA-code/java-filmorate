package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.DbUserRepository;
import ru.yandex.practicum.filmorate.repository.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.service.UserService;

import java.sql.Date;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserService.class, DbUserRepository.class, UserMapper.class, UserRowMapper.class})
public class FilmorateApplicationTests {
    private final UserService userService;


    @Test
    public void testFindUserById() {
        User user = new User();
    /*   // UserDto user = userService.createUser(User user.)

        User newUser = new User();

        user.setName("Name User");
        user.setEmail("user@user.com");
        user.setLogin("UserLogin");
        user.setBirthday(Date.valueOf(LocalDate.of(1981, 5, 1)));
        UserDto user2 = userService.getUserById(1L);
*/
        assertThat(user)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L);
    }


}
