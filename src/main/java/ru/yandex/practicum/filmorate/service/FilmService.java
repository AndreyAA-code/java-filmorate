package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class FilmService {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }

    public Film addLike(Long id, Long userId) {
        filmStorage.getFilms().get(id).getLikes().add(userId);
        log.info("Film: {} successfully got like with user id: {}", filmStorage.getFilms().get(id),userId);
       return filmStorage.getFilms().get(id);
    }

    public Film removeLike(Long id, Long userId) {
        filmStorage.getFilms().get(id).getLikes().remove(userId);
        log.info("Film: {} successfully removed like with user id: {}", filmStorage.getFilms().get(id),userId);
        return filmStorage.getFilms().get(id);
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getFilms().values()
                .stream()
                .sorted(idComparator
                .limit(count)
                .toList();
    }
}
