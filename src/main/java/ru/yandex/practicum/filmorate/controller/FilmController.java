package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/films")
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.debug("Find all films");
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.debug("Create film: {} started", film);
        log.debug("Film: {} send to validation", film);
       // validateFilm(film);
        log.debug("Film: {} successfully validated", film);
        film.setId(getNextId());
        log.debug("Film: {} created with id: {}", film, film.getId());
        films.put(film.getId(), film);
        log.info("Film: {} successfully created with id: {}", film, film.getId());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        log.debug("Update film: {} started", newFilm);
        if (newFilm.getId() == null) {
            log.debug("Film: {} with Id: {} not found in the database", newFilm, newFilm.getId());
            throw new ValidationException("id должен быть указан");
        }

        if (films.containsKey(newFilm.getId())) {
            log.debug("Film: {} with Id: {} found in the database", newFilm, newFilm.getId());
            Film oldFilm = films.get(newFilm.getId());
            log.debug("Film: {} send to validation", newFilm);
           // validateFilm(newFilm);
            log.debug("Film: {} successfully validated", newFilm);
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Film: {} info successfully updated", oldFilm);
            return oldFilm;
        }
        log.debug("Film: {} with Id: {} not found in the database", newFilm, newFilm.getId());
        throw new ValidationException("Такого Id нет");
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
        log.debug("New id: {} succesfully generated", currentMaxId + 1);
        return ++currentMaxId;
    }
}
