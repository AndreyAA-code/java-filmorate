package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FilmService {
    public final FilmRepository filmRepository;
    public final UserRepository userRepository;

    public Collection<FilmDto> getAllFilms() {
        return filmRepository.getAllFilms()
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto addFilm(Film film) {
        return FilmMapper.mapToFilmDto(filmRepository.addFilm(film));
    }

    public FilmDto updateFilm(Film film) {
        return FilmMapper.mapToFilmDto(filmRepository.updateFilm(film));
    }

    public FilmDto getFilmById(Long id) {
        return FilmMapper.mapToFilmDto(filmRepository.getFilmById(id));
    }

    public FilmDto deleteFilmById(Long id) {
        return FilmMapper.mapToFilmDto(filmRepository.deleteFilmById(id));
    }

    public FilmDto likeFilmById(Long filmId, Long userId) {
        return FilmMapper.mapToFilmDto(filmRepository.likeFilmById(filmId, userId));
    }

    public FilmDto deleteLikeUser(@PathVariable Long filmId, @PathVariable Long userId) {
        return FilmMapper.mapToFilmDto(filmRepository.deleteLikeUser(filmId, userId));
    }

    public Collection<FilmDto> getPopularFilms(Long count) {
        return filmRepository.getPopularFilms(count)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public Collection<Genre> getGenres() {
        return filmRepository.getGenres();
    }

    public Genre getGenresById(Long id) {
        return filmRepository.getGenresById(id);
    }

    public Collection<Mpa> getMpas() {
        return filmRepository.getMpas();
    }

    public Mpa getMpaById(Long id) {
        return filmRepository.getMpaById(id);
    }

}
