package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
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
		assertEquals("Name Film Update",filmController.getFilms().get(1L).getName(), "Апдейт имени фильма не получился.");
		assertEquals("Description Film Update",filmController.getFilms().get(1L).getDescription(), "Апдейт описания фильма не получился.");
		assertEquals(Date.valueOf(LocalDate.of(2000, 6, 16)),filmController.getFilms().get(1L).getReleaseDate(), "Апдейт даты выхода фильма не получился.");
		assertEquals(120,filmController.getFilms().get(1L).getDuration(), "Апдейт продолжительности фильма не получился.");
	}

	@Test
	void userCreateTest() {
		User user = new User();
		UserController userController = new UserController();

		user.setId(1L);
		user.setName("Name User");
		user.setEmail("user@user.com");
		user.setLogin("Login User");
		user.setBirthday(Date.valueOf(LocalDate.of(1981, 5, 1)));
		assertNotNull(userController.getUsers().get(1L), "Пользователь не создается.");
	}

	@Test
	void userUpdateTest() {
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
		assertEquals("Name Film Update",filmController.getFilms().get(1L).getName(), "Апдейт фильма не получился.");
	}

}
