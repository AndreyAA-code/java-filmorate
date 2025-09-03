package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmRepository filmRepository;
    private final MpaRepository mpaRepository;
    private final GenreRepository genreRepository;

    public List<FilmDto> findAll() {
        return filmRepository.findAll()
                .stream()
                .map(FilmMapper::mapToFilmDto)
         .collect(Collectors.toList());
    }
    public FilmDto createFilm(NewFilmRequest filmRequest) {

        System.out.println("=== CREATING FILM ===");
        System.out.println("Request MPA: " + filmRequest.getMpa());
        System.out.println("Request MPA ID: " + (filmRequest.getMpa() != null ? filmRequest.getMpa().getId() : "null"));
        if (filmRequest.getName() == null || filmRequest.getName().isEmpty()) {
            throw new ValidationException("Название должно быть указано");
        }
        if (!mpaRepository.existsById(filmRequest.getMpa().getId())) {
            throw new NotFoundException("Неверный MPA");
        }

        Optional<Film> alreadyExistFilm = filmRepository.findByName(filmRequest.getName());
        if (alreadyExistFilm.isPresent()) {
            throw new ValidationException("Такой фильм уже есть");
        }

        Film film = FilmMapper.mapToFilm(filmRequest);
        System.out.println("Mapped film MPA ID: " + film.getMpa().getId());
        film = filmRepository.save(film);
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto updateFilm(UpdateFilmRequest request) {
        System.out.println("Update request: " + request);
        System.out.println("MPA from request: " + request.getMpa());
        Film updatedFilm = filmRepository.findById(request.getId())
                .map(film -> {
                    Film updated = FilmMapper.updateFilmFields(film, request);
                    System.out.println("Updated film before save: " + updated);
                    System.out.println("MPA before save: " + updated.getMpa());
                    return updated;
                })
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        updatedFilm = filmRepository.update(updatedFilm);
        return FilmMapper.mapToFilmDto(updatedFilm);
    }

    public List<Genre> getAllGenres() {
        return genreRepository.getAllGenres();
    }

    public Genre getGenreById(Long id) {
        return genreRepository.getGenreById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с id=" + id + " не найден"));
    }

    public List<Mpa> getAllMpa() {
        return mpaRepository.getAllMpa();
    }

    public Mpa getMpaById(Long id) {
    return mpaRepository.getMpaById(id)
            .orElseThrow(() -> new NotFoundException("MPA с id=" + id + " не найден"));
    }

}

 /*   private final FilmStorage filmStorage;


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

} */
