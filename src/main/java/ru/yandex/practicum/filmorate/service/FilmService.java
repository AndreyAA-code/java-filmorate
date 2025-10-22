package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.repository.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FilmService {
    public final FilmRepository filmRepository;
    public final UserRepository userRepository;
    public final MpaRepository mpaRepository;
    public final GenreRepository genreRepository;
    public final ReviewRepository reviewRepository;
    public final DirectorRepository directorRepository;
    public final FeedRepository feedRepository;

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

    public FilmDto deleteLikeUser(Long filmId, Long userId) {
        return FilmMapper.mapToFilmDto(filmRepository.deleteLikeUser(filmId, userId));
    }

    public Collection<FilmDto> getPopularFilms(Long count, Long genreId, Integer year) {
        return filmRepository.getPopularFilms(count, genreId, year)
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
        userRepository.getUserById(review.getUserId());
        filmRepository.getFilmById(review.getFilmId());
        return reviewRepository.createReview(review);
    }

    public Review updateReview(Review review) {
        return reviewRepository.updateReview(review);
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.deleteReview(reviewId);
    }

    public Review getReviewById(Long id) {
        return reviewRepository.getReviewsById(id);
    }

    public List<Review> getReviews(Optional<Long> filmId, Long count) {
        if (filmId.isPresent()) {
            filmRepository.getFilmById(filmId.get());
        }
        if (count < 0) {
            throw new IllegalArgumentException("count is negative");
        }
        return reviewRepository.getReviews(filmId, count);
    }

    public Review addLikeReview(Long reviewId, Long userId) {
        userRepository.getUserById(userId);
        return reviewRepository.addLikeReview(reviewId, userId);
    }

    public Review addDislikeReview(Long reviewId, Long userId) {
        userRepository.getUserById(userId);
        return reviewRepository.addDislikeReview(reviewId, userId);
    }

    public Review deleteLikeReview(Long reviewId, Long userId) {
        userRepository.getUserById(userId);
        return reviewRepository.deleteLikeReview(reviewId, userId);
    }

    public Review deleteDislikeReview(Long reviewId, Long userId) {
        userRepository.getUserById(userId);
        return reviewRepository.deleteDislikeReview(reviewId, userId);
    }

    public Collection<Director> getDirectors() {
        return directorRepository.getDirectors();
    }

    public Director getDirectorById(Long id) {
        return directorRepository.getDirectorById(id);
    }

    public Director createDirector(Director director) {
        return directorRepository.createDirector(director);
    }

    public Director updateDirector(Director director) {
        return directorRepository.updateDirector(director);
    }

    public void deleteDirector(Long id) {
        directorRepository.deleteDirector(id);
    }

    public Collection<FilmDto> getFilmsByDirector(Long directorId, String sortBy) {
        return filmRepository.getFilmsByDirector(directorId, sortBy)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public Collection<FilmDto> getFilmsBySearch(String query, String by) {
        return filmRepository.getFilmsBySearch(query, by)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public Collection<FilmDto> getCommonFilms(Long userId, Long friendId) {
        userRepository.checkUserId(userId);
        userRepository.checkUserId(friendId);
        return filmRepository.getCommonFilms(userId, friendId)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }
}