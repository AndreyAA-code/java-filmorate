package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmRepository {

    Film addFilm(Film film);

    Collection<Film> getAllFilms();

    Film updateFilm(Film film);

    Film getFilmById(Long id);

    Film deleteFilmById(Long id);

    Film likeFilmById(Long filmId, Long userId);

    Film deleteLikeUser(Long filmId, Long userId);

    Collection<Film> getPopularFilms(Long count);

    Collection<Film> getFilmsByDirector(Long directorId, String sortBy);

}