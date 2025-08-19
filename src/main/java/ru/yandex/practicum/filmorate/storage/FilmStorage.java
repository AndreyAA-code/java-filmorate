package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Set;

@Component
public interface FilmStorage {
    Collection<Film> findAll();

    Film createFilm(Film film);

    Film updateFilm(Film newFilm);

    long getNextId();

    Set<Long> addLike(Long id, Long userId);

    Film removeLike(Long id, Long userId);

    Collection<Film> getPopularFilms(int count);

    void getFilmById(Long id);
}
