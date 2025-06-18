package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.internal.util.Contracts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import java.sql.Date;
import java.time.LocalDate;
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
		FilmController filmController = new FilmController();

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
		FilmController filmController = new FilmController();

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
		UserController userController = new UserController();

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
		UserController userController = new UserController();

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
	void userValidateTest() {
		User user = new User();

		user.setName("");
		user.setEmail("user.com"); //проверяем формат email
		user.setLogin("User Login"); //проверяем пробел в логине
		user.setBirthday(Date.valueOf(LocalDate.of(2030, 5, 1))); //проверяем дату ДР не в будущем

		Set<ConstraintViolation<User>> validates = validator.validate(user);

		Assertions.assertTrue(validates.size() > 0);
		validates.stream()
				.map(v -> v.getMessage())
				.forEach(System.out::println);
	}

	@Test
	void userNamefromLoginIfNameBlankTest() {
		User user = new User();
		UserController userController = new UserController();
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

		Assertions.assertTrue(validates.size() > 0);
		validates.stream()
				.map(v -> v.getMessage())
				.forEach(System.out::println);
	}

}
