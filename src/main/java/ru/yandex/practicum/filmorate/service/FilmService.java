package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;

//переделать на интерфейс
//добавить класс в памяти
//добавить локальное сохранение и загрузку в/из файл

@Service
//@AllArgsConstructor
public class FilmService {

    @Qualifier("inMemoryFilmRepository")

    public final FilmRepository filmRepository;
    public final UserRepository userRepository;

    @Autowired
    public FilmService(FilmRepository filmRepository, UserRepository userRepository) {
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
    }

    public Collection<Film> getAllFilms() {
        return filmRepository.getAllFilms();
    }

    public Film addFilm(Film film) {
        return filmRepository.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmRepository.updateFilm(film);
    }

    public Film getFilmById(Long id) {
        return filmRepository.getFilmById(id);
    }

    public Film deleteFilmById(Long id) {
        return filmRepository.deleteFilmById(id);
    }

    public Film likeFilmById(Long filmId, Long userId) {
        return filmRepository.likeFilmById(filmId, userId);
    }

    public Film deleteLikeUser(@PathVariable Long filmId, @PathVariable Long userId) {
        return filmRepository.deleteLikeUser(filmId, userId);
    }

    public Collection<Film> getPopularFilms(Long count) {
        return filmRepository.getPopularFilms(count);
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
