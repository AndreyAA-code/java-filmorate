package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorRepository {

    Collection<Director> getDirectors();

    Director getDirectorById(Long id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(Long id);

    void checkDirectorId(Long id);

}
