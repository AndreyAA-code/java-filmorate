package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import java.sql.Date;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class FilmorateApplicationTests {

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
		assertNotNull(filmController.getFilms().get(1L), "Фильм не создается.");
	}

	@Test
	void filmUpdateTest() {
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
		assertEquals("Name Film Update", filmController.getFilms().get(1L).getName(), "Апдейт имени фильма не получился.");
		assertEquals("Description Film Update", filmController.getFilms().get(1L).getDescription(), "Апдейт описания фильма не получился.");
		assertEquals(Date.valueOf(LocalDate.of(2000, 6, 16)), filmController.getFilms().get(1L).getReleaseDate(), "Апдейт даты выхода фильма не получился.");
		assertEquals(120, filmController.getFilms().get(1L).getDuration(), "Апдейт продолжительности фильма не получился.");
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
		assertNotNull(userController.getUsers().get(1L), "Пользователь не создается.");
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

		assertEquals("Name User Update", userController.getUsers().get(1L).getName(), "Апдейт имени фильма не получился.");
		assertEquals("updateUser@user.com", userController.getUsers().get(1L).getEmail(), "Апдейт имейла не получился..");
		assertEquals("updateUserLogin", userController.getUsers().get(1L).getLogin(), "Апдейт логина не получился.");
		assertEquals(Date.valueOf(LocalDate.of(2000, 6, 16)), userController.getUsers().get(1L).getBirthday(), "Апдейт даты ДР не получился..");

	}

	@Test
	void userValidateEmailTest() {
		User user = new User();
		UserController userController = new UserController();

		user.setName("");
		user.setEmail("user.com");
		user.setLogin("User Login");
		user.setBirthday(Date.valueOf(LocalDate.of(2981, 5, 1)));
		try {
			userController.create(user);
		} catch (ValidationException e) {
			System.out.println(e.getMessage());
			assertEquals("неправильный формат имейл адреса", e.getMessage(), "Не ловит ошибку@.");
		}
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

		assertEquals("UserLogin", userController.getUsers().get(1L).getName(), "Пустое имя не меняется на логин.");
	}

	@Test
	void userBirthdayCantBeInFutureTest() {
		User user = new User();
		UserController userController = new UserController();

		user.setName("Name");
		user.setEmail("user@com");
		user.setLogin("UserLogin");
		user.setBirthday(Date.valueOf(LocalDate.of(2981, 5, 1)));

		try {
			userController.create(user);
		} catch (ValidationException e) {
			System.out.println(e.getMessage());
			assertEquals("дата рождения не может быть в будущем", e.getMessage(), "Не ловит ошибку cне наступившей датой ДР.");
		}
	}

	@Test
	void userLoginCantContainSpacesTest() {
		User user = new User();
		UserController userController = new UserController();

		user.setName("Name");
		user.setEmail("user@com");
		user.setLogin("User Login");
		user.setBirthday(Date.valueOf(LocalDate.of(1981, 5, 1)));

		try {
			userController.create(user);
		} catch (ValidationException e) {
			System.out.println(e.getMessage());
			assertEquals("login не должен быть пустым или содержать пробелы", e.getMessage(), "Не ловит ошибку пробелов в логине.");
		}
	}

	@Test
	void filmNameCantBeBlankTest() {
		Film film = new Film();
		FilmController filmController = new FilmController();

		film.setName("");
		film.setDescription("Description Film");
		film.setReleaseDate(Date.valueOf(LocalDate.of(1987, 5, 1)));
		film.setDuration(90);
		try {
			filmController.createFilm(film);
		} catch (ValidationException e) {
			assertEquals("Имя не может быть пустым", e.getMessage(), "Пустое имя не отлавливается.");
		}
	}

	@Test
	void filmDescriptionCantBeMoreThan200SymbolsTest() {
		Film film = new Film();
		FilmController filmController = new FilmController();

		film.setName("film");
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
		film.setReleaseDate(Date.valueOf(LocalDate.of(1987, 5, 1)));
		film.setDuration(90);
		try {
			filmController.createFilm(film);
		} catch (ValidationException e) {
			assertEquals("Описание превышает 200 символов", e.getMessage(), "Превышение длины описания не отлавливается.");
		}
	}

	@Test
	void filmReleaseDateCantBeLessThanTest() {
		Film film = new Film();
		FilmController filmController = new FilmController();

		film.setName("Film");
		film.setDescription("Description Film");
		film.setReleaseDate(Date.valueOf(LocalDate.of(1894, 5, 1)));
		film.setDuration(90);
		try {
			filmController.createFilm(film);
		} catch (ValidationException e) {
			assertEquals("Ошибка в дате релиза фильма", e.getMessage(), "Неправильная дата релиза не отлавливается.");
		}
	}

	@Test
	void filmDurationCantBeNegativeTest() {
		Film film = new Film();
		FilmController filmController = new FilmController();

		film.setName("Film");
		film.setDescription("Description Film");
		film.setReleaseDate(Date.valueOf(LocalDate.of(1987, 5, 1)));
		film.setDuration(-1);
		try {
			filmController.createFilm(film);
		} catch (ValidationException e) {
			assertEquals("Продолжительность фильма не может быть отрицательной", e.getMessage(), "Отрицательная продолжительность фильма не отлавливается.");
		}
	}

}
