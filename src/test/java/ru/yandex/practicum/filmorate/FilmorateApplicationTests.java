package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;


class FilmorateApplicationTests {

	private static Validator validator;
	private FilmController filmController;
	private UserController userController;

	@BeforeAll
	static void setUpValidator() {
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
		validator = validatorFactory.usingContext().getValidator();
	}
}
/*
	void setUpControllers() {
		UserStorage userStorage = new InMemoryUserStorage();
		InMemoryFilmStorage filmStorage = new InMemoryFilmStorage(userStorage);
		FilmService filmService = new FilmService(filmStorage);
		UserService userService = new UserService(userStorage);
		filmController = new FilmController(filmService);
		userController = new UserController(userService);
	}


	@Test
	void filmCreateTest() {
		setUpControllers();
		Film film = new Film();
		film.setName("Name Film");
		film.setDescription("Description Film");
		film.setReleaseDate(Date.valueOf(LocalDate.of(1987, 5, 1)));
		film.setDuration(90);

		Film createdFilm = filmController.createFilm(film);
		assertNotNull(createdFilm.getId(), "Фильм не создается.");
	}

	@Test
	void filmUpdateTest() throws ValidationException {
		setUpControllers();
		Film film = new Film();
		film.setName("Name Film");
		film.setDescription("Description Film");
		film.setReleaseDate(Date.valueOf(LocalDate.of(1987, 5, 1)));
		film.setDuration(90);
		filmController.createFilm(film);

		Film newFilm = new Film();
		newFilm.setId(film.getId());
		newFilm.setName("Name Film Update");
		newFilm.setDescription("Description Film Update");
		newFilm.setReleaseDate(Date.valueOf(LocalDate.of(2000, 6, 16)));
		newFilm.setDuration(120);

		Film updatedFilm = filmController.updateFilm(newFilm);
		assertEquals("Name Film Update", updatedFilm.getName(), "Апдейт имени фильма не получился.");
		assertEquals("Description Film Update", updatedFilm.getDescription(), "Апдейт описания фильма не получился.");
		assertEquals(Date.valueOf(LocalDate.of(2000, 6, 16)), updatedFilm.getReleaseDate(), "Апдейт даты выхода фильма не получился.");
		assertEquals(120, updatedFilm.getDuration(), "Апдейт продолжительности фильма не получился.");
	}

	@Test
	void userCreateTest() {
		setUpControllers();
		User user = new User();
		User createdUser = userController.create(user);
		assertNotNull(createdUser.getId(), "Пользователь не создается.");
	}

	@Test
	void userUpdateTest() {
		setUpControllers();
		User user = new User();
		User createdUser = userController.create(user);

		User updatedUser = new User();
		updatedUser.setId(createdUser.getId());
		updatedUser.setName("Updated Name");
		updatedUser.setEmail("updated@email.com");
		updatedUser.setLogin("updatedLogin");
		updatedUser.setBirthday(Date.valueOf(LocalDate.of(1990, 1, 1)));

		User result = userController.update(updatedUser);
		assertEquals(updatedUser.getName(), result.getName());
		assertEquals(updatedUser.getEmail(), result.getEmail());
		assertEquals(updatedUser.getLogin(), result.getLogin());
		assertEquals(updatedUser.getBirthday(), result.getBirthday());
	}


	@Test
	void userUpdateTestUserController() {
		setUpControllers();
		User user = new User();
		User newUser = new User();

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
		setUpControllers();
		User user1 = new User();
		User user2 = new User();
		User user3 = new User();

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


		userController.addFriend(user1.getId(), user2.getId());
		userController.addFriend(user1.getId(), user3.getId());
		userController.addFriend(user2.getId(), user3.getId());

		List<User> commonFriends = userController.getCommonFriends(user1.getId(), user2.getId());

		assertEquals(1, commonFriends.size());
		assertTrue(commonFriends.contains(user3));

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
		setUpControllers();
		User user = new User();

		user.setName("");
		user.setEmail("user@user.com");
		user.setLogin("UserLogin");
		user.setBirthday(Date.valueOf(LocalDate.of(1981, 5, 1)));
		userController.create(user);

		assertEquals("UserLogin", user.getName(), "Пустое имя не меняется на логин.");
	}

	@Test
	void filmValidateTest() {
		setUpControllers();
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

} */
