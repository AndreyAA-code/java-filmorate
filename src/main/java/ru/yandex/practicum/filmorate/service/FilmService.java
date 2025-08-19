package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmStorage filmStorage;


    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }

    public Set<Long> addLike(Long id, Long userId) {
        return filmStorage.addLike(id, userId);
    }

    public Film removeLike(Long id, Long userId) {
        return filmStorage.removeLike(id, userId);
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

}
