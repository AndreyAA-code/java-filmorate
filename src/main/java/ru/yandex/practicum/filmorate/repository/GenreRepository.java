package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreRepository {

    Collection<Genre> getGenres();

    Genre getGenresById(Long id);

    void checkGenreId(Long id);
}
