package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@Primary
@RequiredArgsConstructor
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Comparator<Film> likesComparator = Comparator.comparing(Film::getLikesSize);

    private final Map<Long, Film> films = new HashMap<>();
    private final UserStorage userStorage;

    @Override
    public Collection<Film> findAll() {
        log.debug("Find all films");
        return films.values();
    }

    @Override
    public Film createFilm(Film film) {
        log.debug("Create film: {} started", film);
        log.debug("Film: {} send to validation", film);
        log.debug("Film: {} successfully validated", film);
        film.setId(getNextId());
        log.debug("Film: {} created with id: {}", film, film.getId());
        films.put(film.getId(), film);
        log.info("Film: {} successfully created with id: {}", film, film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        log.debug("Update film: {} started", newFilm);
        if (newFilm.getId() == null) {
            log.debug("Film: {} with Id: {} not found in the database", newFilm, newFilm.getId());
            throw new NotFoundException("id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            log.debug("Film: {} with Id: {} found in the database", newFilm, newFilm.getId());
            Film oldFilm = films.get(newFilm.getId());
            log.debug("Film: {} send to validation", newFilm);
            log.debug("Film: {} successfully validated", newFilm);
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Film: {} info successfully updated", oldFilm);
            return oldFilm;
        }
        log.debug("Film: {} with Id: {} not found in the database", newFilm, newFilm.getId());
        throw new NotFoundException("Такого Id нет");
    }

    @Override
    public long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
        log.debug("New id: {} succesfully generated", currentMaxId + 1);
        return ++currentMaxId;
    }

    @Override
    public Set<Long> addLike(Long id, Long userId) {
        getFilmById(id);
        films.get(id).getLikes().add(userId);
        log.info("Film: {} successfully got like with user id: {}", films.get(id), userId);
        return films.get(id).getLikes();
    }

    @Override
    public Film removeLike(Long id, Long userId) {
        getFilmById(id);
        if (films.get(id).getLikes().contains(userId)) {
            films.get(id).getLikes().remove(userId);
            log.info("Film: {} successfully removed like with user id: {}", films.get(id), userId);
            return films.get(id);
        }
        throw new NotFoundException("UserId not found");
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        return films.values()
                .stream()
                .sorted(likesComparator.reversed())
                .limit(count)
                .toList();
    }

    @Override
    public void getFilmById(Long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Film with id " + id + " not found");
        }
    }
}
