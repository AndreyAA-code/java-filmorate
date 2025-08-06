package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmorateApplicationTests {

	private static Validator validator;

	static {
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
		validator = validatorFactory.usingContext().getValidator();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void filmCreateTest() {
		Film film = new Film();
		FilmService filmService = new FilmService();
		FilmController filmController = new FilmController(filmService);

		film.setId(1L);
		film.setName("Name Film");
		film.setDescription("Description Film");
		film.setReleaseDate(Date.valueOf(LocalDate.of(1987, 5, 1)));
		film.setDuration(90);
		filmController.createFilm(film);
		assertNotNull(film.getId(), "Фильм не создается.");
	}

	@Test
	void filmUpdateTest() throws ValidationException {
		Film newFilm = new Film();
		Film film = new Film();
		FilmService filmService = new FilmService();
		FilmController filmController = new FilmController(filmService);

		film.setId(1L);
		film.setName("Name Film");
		film.setDescription("Description Film");
		film.setReleaseDate(Date.valueOf(LocalDate.of(1987, 5, 1)));
		film.setDuration(90);
		filmController.createFilm(film);

		newFilm.setId(1L);
		newFilm.setName("Name Film Update");
		newFilm.setDescription("Description Film Update");
		newFilm.setReleaseDate(Date.valueOf(LocalDate.of(2000, 6, 16)));
		newFilm.setDuration(120);
		filmController.updateFilm(newFilm);
		assertEquals("Name Film Update", film.getName(), "Апдейт имени фильма не получился.");
		assertEquals("Description Film Update", film.getDescription(), "Апдейт описания фильма не получился.");
		assertEquals(Date.valueOf(LocalDate.of(2000, 6, 16)), film.getReleaseDate(), "Апдейт даты выхода фильма не получился.");
		assertEquals(120, film.getDuration(), "Апдейт продолжительности фильма не получился.");
	}

	@Test
	void userCreateTest() {
		User user = new User();
		UserService userService = new UserService();
		UserController userController = new UserController(userService);

		user.setName("Name User");
		user.setEmail("user@user.com");
		user.setLogin("UserLogin");
		user.setBirthday(Date.valueOf(LocalDate.of(1981, 5, 1)));
		userController.create(user);
		assertNotNull(user.getId(), "Пользователь не создается.");
	}

	@Test
	void userUpdateTest() {
		User user = new User();
		User newUser = new User();
		UserService userService = new UserService();
		UserController userController = new UserController(userService);

		user.setName("Name User");
		user.setEmail("user@user.com");
		user.setLogin("UserLogin");
		user.setBirthday(Date.valueOf(LocalDate.of(1981, 5, 1)));
		userController.create(user);

		newUser.setId(1L);
		newUser.setName("Name User Update");
		newUser.setBirthday(Date.valueOf(LocalDate.of(2000, 6, 16)));
		newUser.setEmail("updateUser@user.com");
		newUser.setLogin("updateUserLogin");
		userController.update(newUser);

		assertEquals("Name User Update", user.getName(), "Апдейт имени фильма не получился.");
		assertEquals("updateUser@user.com", user.getEmail(), "Апдейт имейла не получился..");
		assertEquals("updateUserLogin", user.getLogin(), "Апдейт логина не получился.");
		assertEquals(Date.valueOf(LocalDate.of(2000, 6, 16)), user.getBirthday(), "Апдейт даты ДР не получился..");

	}

	@Test
	void userFriendsTest() {
		User user1 = new User();
		User user2 = new User();
		User user3 = new User();
		UserService userService = new UserService();
		UserController userController = new UserController(userService);

		user1.setName("Name User");
		user1.setEmail("user1@user.com");
		user1.setLogin("UserLogin1");
		user1.setBirthday(Date.valueOf(LocalDate.of(1981, 5, 1)));
		userController.create(user1);

		user2.setName("Name User");
		user2.setEmail("user2@user.com");
		user2.setLogin("UserLogin2");
		user2.setBirthday(Date.valueOf(LocalDate.of(1981, 5, 1)));
		userController.create(user2);

		user3.setName("Name User");
		user3.setEmail("user3@user.com");
		user3.setLogin("UserLogin3");
		user3.setBirthday(Date.valueOf(LocalDate.of(1981, 5, 1)));
		userController.create(user3);
		Set<Long> set1 = new HashSet<>();
		Set<Long> set2 = new HashSet<>();
		set2.add(2L);


		userController.addFriend(1L,2L);
		userController.addFriend(1L,3L);
		userController.addFriend(2L,3L);
		set1 = userController.getCommonFriends(1L,3L);

		assertNotNull(user1.getId(), "Пользователь не создается.");
		assertNotNull(user2.getId(), "Пользователь не создается.");
		assertNotNull(user3.getId(), "Пользователь не создается.");
		assertEquals(set2, set1, "Поиск общих друзей ошибочен");

	}
	@Test
	void userValidateTest() {
		User user = new User();

		user.setName("");
		user.setEmail("user.com"); //проверяем формат email
		user.setLogin("User Login"); //проверяем пробел в логине
		user.setBirthday(Date.valueOf(LocalDate.of(2030, 5, 1))); //проверяем дату ДР не в будущем

		Set<ConstraintViolation<User>> validates = validator.validate(user);

		assertTrue(validates.size() > 0);
		validates.stream()
				.map(v -> v.getMessage())
				.forEach(System.out::println);
	}

	@Test
	void userNamefromLoginIfNameBlankTest() {
		User user = new User();

		UserService userService = new UserService();
		UserController userController = new UserController(userService);
		user.setName("");
		user.setEmail("user@user.com");
		user.setLogin("UserLogin");
		user.setBirthday(Date.valueOf(LocalDate.of(1981, 5, 1)));
		userController.create(user);

		assertEquals("UserLogin", user.getName(), "Пустое имя не меняется на логин.");
	}

	@Test
	void filmValidateTest() {
		Film film = new Film();

		film.setName("");
		film.setDescription("Description Film Description FilmDescription FilmDescription FilmDescription FilmDescripti" +
				"on FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription " +
						"on FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription " +
						"on FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription " +
						"on FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription " +
						"on FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription " +
						"on FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription " +
						"on FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription " +
						"on FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription " +
						"on FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription " +
						"on FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription " +
						"on FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription " +
						"on FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription " +
						"on FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription FilmDescription ");
		film.setReleaseDate(Date.valueOf(LocalDate.of(1817, 5, 1)));
		film.setDuration(-1);

		Set<ConstraintViolation<Film>> validates = validator.validate(film);

		assertTrue(validates.size() > 0);
		validates.stream()
				.map(v -> v.getMessage())
				.forEach(System.out::println);
	}

}
