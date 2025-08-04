package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

@Component
public interface FilmStorage {
    Collection<Film> findAll();

    Film createFilm(Film film);

    Film updateFilm(Film newFilm);

    long getNextId();
}
