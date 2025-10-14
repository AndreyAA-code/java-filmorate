package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FilmService {
    public final FilmRepository filmRepository;
    public final UserRepository userRepository;
    public final MpaRepository mpaRepository;
    public final GenreRepository genreRepository;
    public final ReviewRepository reviewRepository;

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
        return genreRepository.getGenres();
    }

    public Genre getGenresById(Long id) {
        return genreRepository.getGenresById(id);
    }

    public Collection<Mpa> getMpas() {
        return mpaRepository.getMpas();
    }

    public Mpa getMpaById(Long id) {
        return mpaRepository.getMpaById(id);
    }

    public Review createReview(Review review) {
        return reviewRepository.createReview(review);
    }

    public Review updateReview(Review review) {
        return null;
    }

    public Review deleteReview(Review review) {
        return null;
    }

    public Review getReviewById(Long id) {
        return reviewRepository.getReviewsById(id);
    }

    public List<Review> getReviews() {
        return null;
    }

    public List<Review> getReviews(Long filmId, Long count) {
        return null;
    }

    public Review addLikeReview(Long id, Long userId) {
        return null;
    }

    public Review addDislikeReview(Long id, Long userId) {
        return null;
    }

    public Review deleteLikeReview(Long id, Long userId) {
        return null;
    }
    public Review deleteDislikeReview(Long id, Long userId) {
        return null;
    }

}