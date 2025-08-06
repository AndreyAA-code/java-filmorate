package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Component
public class InMemoryFilmStorage {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    public Map<Long, Film> getFilms() {
        return films;
    }

    private final Map<Long, Film> films = new HashMap<>();

   // @Override
    public Collection<Film> findAll() {
        log.debug("Find all films");
        return films.values();
    }

   // @Override
    public Film createFilm(Film film) {
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

  //  @Override
    public Film updateFilm(Film newFilm) {
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

   // @Override
    public long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
        log.debug("New id: {} succesfully generated", currentMaxId + 1);
        return ++currentMaxId;
    }




}
