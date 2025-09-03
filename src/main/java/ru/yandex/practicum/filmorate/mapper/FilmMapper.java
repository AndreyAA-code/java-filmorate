package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

public class FilmMapper {
    public static FilmDto mapToFilmDto(Film film) {

        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDescription(film.getDescription());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setDuration(film.getDuration());
        filmDto.setMpa(film.getMpa());
        filmDto.setGenres(film.getGenres());
        return filmDto;
    }

    public static Film mapToFilm(NewFilmRequest request) {
        System.out.println("Mapping request to film");
        System.out.println("Request MPA ID: " + (request.getMpa() != null ? request.getMpa().getId() : "null"));
        Film film = new Film();
        film.setId(request.getId());
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
       // film.setMpa(request.getMpa());
        film.setGenres(request.getGenres());
        if (request.getMpa() != null) {
            Mpa mpa = new Mpa();
            mpa.setId(request.getMpa().getId());
            film.setMpa(mpa);
            System.out.println("Set MPA ID: " + mpa.getId());
        }
        return film;
    }
    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {

        if (request.hasName()) {
            film.setName(request.getName());
        }
        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }
        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }

        if (request.hasDuration()) {
            film.setDuration(request.getDuration());
        }
        if (request.hasMpa()) {
            Mpa mpa = new Mpa();
            mpa.setId(request.getMpa().getId());
            film.setMpa(mpa);
            System.out.println("Updated MPA to: " + mpa.getId());
        }
        return film;
    }
}
