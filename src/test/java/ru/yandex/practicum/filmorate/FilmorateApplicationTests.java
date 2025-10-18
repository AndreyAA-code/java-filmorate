package ru.yandex.practicum.filmorate;

import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.DbRepositories.*;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.repository.mappers.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({DbUserRepository.class, DbFilmRepository.class, DbMpaRepository.class, DbGenreRepository.class, DbFeedRepository.class, DbReviewRepository.class, UserRowMapper.class,
        FilmRowMapper.class, GenreRowMapper.class, MpaRowMapper.class, DirectorRowMapper.class, DbDirectorRepository.class, ReviewRowMapper.class, UserEventRowMapper.class})
class FilmorateApplicationTests {

    private final DbUserRepository dbUserRepository;
    private final JdbcTemplate jdbcTemplate;
    private FilmController filmController;
    private UserController userController;
    private static Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @BeforeEach
    @Sql(scripts = "/testData.sql")
    void setUpControllers() {

        final UserRowMapper userRowMapper = new UserRowMapper();
        final FilmRowMapper filmRowMapper = new FilmRowMapper();
        final GenreRowMapper genreRowMapper = new GenreRowMapper();
        final MpaRowMapper mpaRowMapper = new MpaRowMapper();
        final ReviewRowMapper reviewRowMapper = new ReviewRowMapper();
        final DirectorRowMapper directorRowMapper = new DirectorRowMapper();
        final UserEventRowMapper userEventRowMapper = new UserEventRowMapper();

        DbMpaRepository dbMpaRepository = new DbMpaRepository(jdbcTemplate, mpaRowMapper);
        DbGenreRepository dbGenreRepository = new DbGenreRepository(jdbcTemplate, genreRowMapper);
        DbDirectorRepository dbDirectorRepository = new DbDirectorRepository(jdbcTemplate, directorRowMapper);
        DbFeedRepository dbFeedRepository = new DbFeedRepository(jdbcTemplate, userEventRowMapper);
        UserRepository userRepository = new DbUserRepository(jdbcTemplate, userRowMapper, dbFeedRepository);
        ReviewRepository dbReviewRepository = new DbReviewRepository(jdbcTemplate, reviewRowMapper, dbFeedRepository);
        FilmRepository filmRepository = new DbFilmRepository(jdbcTemplate, filmRowMapper, userRowMapper, dbMpaRepository, dbGenreRepository, dbDirectorRepository, dbFeedRepository);
        FilmService filmService = new FilmService(filmRepository, userRepository, dbMpaRepository, dbGenreRepository, dbReviewRepository, dbDirectorRepository, dbFeedRepository);
        UserService userService = new UserService(userRepository, dbFeedRepository);
        filmController = new FilmController(filmService);
        userController = new UserController(userService);
    }

    @Test
    @Sql(scripts = "/testData.sql")
    public void testFindUserById() {

        System.out.println(dbUserRepository.getAllUsers());
        Optional<User> userOptional = Optional.ofNullable(dbUserRepository.getUserById(1L));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void filmCreateTest() {

        Film film = new Film();
        film.setName("Name Film");
        film.setDescription("Description Film");
        film.setReleaseDate(Date.valueOf(LocalDate.of(1987, 5, 1)).toLocalDate());
        film.setDuration(90L);
        film.setMpa(new Mpa(3L, "PG-13"));
        FilmDto filmDto = filmController.addFilm(film);
        assertNotNull(filmDto.getId(), "Фильм не создается.");
    }

    @Test
    void filmUpdateTest() throws ValidationException {

        Film film = new Film();
        film.setName("Name Film");
        film.setDescription("Description Film");
        film.setReleaseDate(Date.valueOf(LocalDate.of(1987, 5, 1)).toLocalDate());
        film.setDuration(90L);
        film.setMpa(new Mpa(3L, "PG-13"));
        filmController.addFilm(film);

        Film newFilm = new Film();
        newFilm.setId(film.getId());
        newFilm.setName("Name Film Update");
        newFilm.setDescription("Description Film Update");
        newFilm.setReleaseDate(Date.valueOf(LocalDate.of(2000, 6, 16)).toLocalDate());
        newFilm.setDuration(120L);
        newFilm.setMpa(new Mpa(4L, "R"));

        FilmDto updatedFilm = filmController.updateFilm(newFilm);
        assertEquals("Name Film Update", updatedFilm.getName(), "Апдейт имени фильма не получился.");
        assertEquals("Description Film Update", updatedFilm.getDescription(), "Апдейт описания фильма не получился.");
        assertEquals(LocalDate.of(2000, 6, 16), updatedFilm.getReleaseDate(), "Апдейт даты выхода фильма не получился.");
        assertEquals(120, updatedFilm.getDuration(), "Апдейт продолжительности фильма не получился.");
    }

    @Test
    void userCreateTest() {

        User user = new User();
        user.setName("Name User");
        user.setBirthday(Date.valueOf(LocalDate.of(1987, 5, 1)).toLocalDate());
        user.setLogin("login");
        user.setEmail("email@ya.com");
        UserDto createdUser = userController.createUser(user);

        assertNotNull(createdUser.getId(), "Пользователь не создается.");
    }

    @Test
    void userUpdateTest() {

        User user = new User();
        user.setName("Name User");
        user.setBirthday(Date.valueOf(LocalDate.of(1987, 5, 1)).toLocalDate());
        user.setLogin("login");
        user.setEmail("email@ya.com");
        UserDto createdUser = userController.createUser(user);

        User updatedUser = new User();
        updatedUser.setId(createdUser.getId());
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("updated@email.com");
        updatedUser.setLogin("updatedLogin");
        updatedUser.setBirthday(Date.valueOf(LocalDate.of(1990, 1, 1)).toLocalDate());

        UserDto result = userController.updateUser(updatedUser);
        assertEquals(updatedUser.getName(), result.getName());
        assertEquals(updatedUser.getEmail(), result.getEmail());
        assertEquals(updatedUser.getLogin(), result.getLogin());
        assertEquals(updatedUser.getBirthday(), result.getBirthday());
    }

    @Test
    void userFriendsTest() {

        User user1 = new User();
        User user2 = new User();
        User user3 = new User();

        user1.setName("Name User");
        user1.setEmail("user1@user.com");
        user1.setLogin("UserLogin1");
        user1.setBirthday(Date.valueOf(LocalDate.of(1981, 5, 1)).toLocalDate());
        userController.createUser(user1);

        user2.setName("Name User");
        user2.setEmail("user2@user.com");
        user2.setLogin("UserLogin2");
        user2.setBirthday(Date.valueOf(LocalDate.of(1981, 5, 1)).toLocalDate());
        userController.createUser(user2);

        user3.setName("Name User");
        user3.setEmail("user3@user.com");
        user3.setLogin("UserLogin3");
        user3.setBirthday(Date.valueOf(LocalDate.of(1981, 5, 1)).toLocalDate());
        userController.createUser(user3);
        Set<Long> set1 = new HashSet<>();
        Set<Long> set2 = new HashSet<>();
        set2.add(2L);


        userController.updateUserFriends(user1.getId(), user2.getId());
        userController.updateUserFriends(user1.getId(), user3.getId());
        userController.updateUserFriends(user2.getId(), user3.getId());

        Set<UserDto> commonFriends = userController.getCommonFriends(user1.getId(), user2.getId());
        System.out.println(commonFriends);
        UserDto userDto1 = UserMapper.mapToUserDto(user3);
        assertEquals(1, commonFriends.size());
        assertTrue(commonFriends.contains(userDto1));
    }

    @Test
    void userValidateTest() {
        User user = new User();

        user.setName("");
        user.setEmail("user.com"); //проверяем формат email
        user.setLogin("User Login"); //проверяем пробел в логине
        user.setBirthday(Date.valueOf(LocalDate.of(2030, 5, 1)).toLocalDate()); //проверяем дату ДР не в будущем

        Set<ConstraintViolation<User>> validates = validator.validate(user);

        assertTrue(validates.size() > 0);
        validates.stream()
                .map(v -> v.getMessage())
                .forEach(System.out::println);
    }

    @Test
    void userNameFromLoginIfNameBlankTest() {
        User user = new User();
        user.setName("");
        user.setEmail("user@user.com");
        user.setLogin("UserLogin");
        user.setBirthday(LocalDate.of(1981, 5, 1));

        UserDto createdUser = userController.createUser(user);
        assertEquals("UserLogin", createdUser.getName(), "Пустое имя не меняется на логин.");
    }

    @Test
    void filmValidateTest() {
        Film film = new Film();

        film.setName("");
        film.setDescription("A".repeat(201)); // Проверяем строку более 200 символов
        film.setReleaseDate(Date.valueOf(LocalDate.of(1817, 5, 1)).toLocalDate());
        film.setDuration(-1L);
        film.setMpa(new Mpa(1L, "G"));

        Set<ConstraintViolation<Film>> validates = validator.validate(film);

        assertTrue(validates.size() > 0);
        validates.stream()
                .map(v -> v.getMessage())
                .forEach(System.out::println);
    }

    @Test
    void filmLikesTest() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120L);
        film.setMpa(new Mpa(1L, "G"));
        FilmDto filmDto = filmController.addFilm(film);

        User user = new User();
        user.setName("Name User");
        user.setBirthday(Date.valueOf(LocalDate.of(1987, 5, 1)).toLocalDate());
        user.setLogin("login");
        user.setEmail("email@ya.com");
        UserDto createdUser = userController.createUser(user);

        filmController.likeFilmById(filmDto.getId(), createdUser.getId());

        FilmDto updatedFilm = filmController.getFilmById(filmDto.getId());
        assertTrue(updatedFilm.getLikes().contains(createdUser.getId()), "Лайк пользователя не добавлен");

    }
}