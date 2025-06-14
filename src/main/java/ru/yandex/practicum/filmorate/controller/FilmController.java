package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.debug("Find all films");
        return films.values();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.debug("Create film: {} started", film);
        log.debug("Film: {} send to validation", film);
        validateFilm(film);
        log.debug("Film: {} successfully validated", film);
        film.setId(getNextId());
        log.debug("Film: {} created with id: {}", film, film.getId());
        films.put(film.getId(), film);
        log.info("Film: {} successfully created with id: {}", film, film.getId());
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        log.debug("Update film: {} started", newFilm);
        if (newFilm.getId() == null) {
            log.debug("Film: {} with Id: {} not found in the database", newFilm, newFilm.getId());
            throw new ValidationException("id должен быть указан");
        }

        if (films.containsKey(newFilm.getId())) {
            log.debug("Film: {} with Id: {} found in the database", newFilm, newFilm.getId());
            Film oldFilm = films.get(newFilm.getId());
            log.debug("Film: {} send to validation", newFilm);
            validateFilm(newFilm);
            log.debug("Film: {} successfully validated", newFilm);
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            log.debug("Film: {} info successfully updated", oldFilm);
            return oldFilm;
        }
        log.debug("Film: {} with Id: {} not found in the database", newFilm, newFilm.getId());
        throw new ValidationException("Такого Id нет");
    }

    private Film validateFilm(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            throw new ValidationException("Имя не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Описание превышает 200 символов");
        }
        if (film.getReleaseDate().before(Date.valueOf(LocalDate.of(1895,12,28)))){
            throw new ValidationException("Ошибка в дате релиза фильма");
        }
        if (film.getDuration() <0) {
            throw new ValidationException("Продолжительность фильма не может быть отрицательной");
        }
        return film;
    }

    private long getNextId() {
    long currentMaxId = films.keySet()
            .stream()
            .mapToLong(id->id)
            .max()
            .orElse(0L);
    log.debug("New id: {} succesfully generated", ++currentMaxId);
    return ++currentMaxId;
    }
}
