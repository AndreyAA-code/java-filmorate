package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.repository.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
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
        log.info("Get all films");
        return filmRepository.getAllFilms()
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto addFilm(Film film) {
        log.info("Add film");
        return FilmMapper.mapToFilmDto(filmRepository.addFilm(film));
    }

    public FilmDto updateFilm(Film film) {
        log.info("Update film");
        return FilmMapper.mapToFilmDto(filmRepository.updateFilm(film));
    }

    public FilmDto getFilmById(Long id) {
        log.info("Get film by id {}", id);
        return FilmMapper.mapToFilmDto(filmRepository.getFilmById(id));
    }

    public FilmDto deleteFilmById(Long id) {
        log.info("Delete film by id {}", id);
        return FilmMapper.mapToFilmDto(filmRepository.deleteFilmById(id));
    }

    public FilmDto likeFilmById(Long filmId, Long userId) {
        log.info("Like film by id {}", filmId);
        return FilmMapper.mapToFilmDto(filmRepository.likeFilmById(filmId, userId));
    }

    public FilmDto deleteLikeUser(Long filmId, Long userId) {
        log.info("Delete like film by id {}", filmId);
        return FilmMapper.mapToFilmDto(filmRepository.deleteLikeUser(filmId, userId));
    }

    public Collection<FilmDto> getPopularFilms(Long count, Long genreId, Integer year) {
        log.info("Get popular films");
        return filmRepository.getPopularFilms(count, genreId, year)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public Collection<Genre> getGenres() {
        log.info("Get genres");
        return genreRepository.getGenres();
    }

    public Genre getGenresById(Long id) {
        log.info("Get genres by id {}", id);
        return genreRepository.getGenresById(id);
    }

    public Collection<Mpa> getMpas() {
        log.info("Get mpas");
        return mpaRepository.getMpas();
    }

    public Mpa getMpaById(Long id) {
        log.info("Get mpa with id {}", id);
        return mpaRepository.getMpaById(id);
    }

    public Review createReview(Review review) {
        log.info("Create review {}", review);
        userRepository.getUserById(review.getUserId());
        filmRepository.getFilmById(review.getFilmId());
        return reviewRepository.createReview(review);
    }

    public Review updateReview(Review review) {
        log.info("Update review {}", review);
        return reviewRepository.updateReview(review);
    }

    public void deleteReview(Long reviewId) {
        log.info("Delete review {}", reviewId);
        reviewRepository.deleteReview(reviewId);
    }

    public Review getReviewById(Long id) {
        log.info("Get review by id {}", id);
        return reviewRepository.getReviewsById(id);
    }

    public List<Review> getReviews(Optional<Long> filmId, Long count) {
        log.info("Get reviews by film with id {}", filmId);
        if (filmId.isPresent()) {
            filmRepository.getFilmById(filmId.get());
        }
        if (count < 0) {
            throw new IllegalArgumentException("count is negative");
        }
        return reviewRepository.getReviews(filmId, count);
    }

    public Review addLikeReview(Long reviewId, Long userId) {
        log.info("Add like review {}", reviewId);
        userRepository.getUserById(userId);
        return reviewRepository.addLikeReview(reviewId, userId);
    }

    public Review addDislikeReview(Long reviewId, Long userId) {
        log.info("Add dislike review {}", reviewId);
        userRepository.getUserById(userId);
        return reviewRepository.addDislikeReview(reviewId, userId);
    }

    public Review deleteLikeReview(Long reviewId, Long userId) {
        log.info("Delete like review {}", reviewId);
        userRepository.getUserById(userId);
        return reviewRepository.deleteLikeReview(reviewId, userId);
    }

    public Review deleteDislikeReview(Long reviewId, Long userId) {
        log.info("Delete dislike review {}", reviewId);
        userRepository.getUserById(userId);
        return reviewRepository.deleteDislikeReview(reviewId, userId);
    }

    public Collection<Director> getDirectors() {
        log.info("Get directors");
        return directorRepository.getDirectors();
    }

    public Director getDirectorById(Long id) {
        log.info("Get director by id {}", id);
        return directorRepository.getDirectorById(id);
    }

    public Director createDirector(Director director) {
        log.info("Create director {}", director);
        return directorRepository.createDirector(director);
    }

    public Director updateDirector(Director director) {
        log.info("Update director {}", director);
        return directorRepository.updateDirector(director);
    }

    public void deleteDirector(Long id) {
        log.info("Delete director {}", id);
        directorRepository.deleteDirector(id);
    }

    public Collection<FilmDto> getFilmsByDirector(Long directorId, String sortBy) {
        log.info("Get films by director {}", directorId);
        return filmRepository.getFilmsByDirector(directorId, sortBy)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public Collection<FilmDto> getFilmsBySearch(String query, String by) {
        log.info("Get films by query {}", query);
        return filmRepository.getFilmsBySearch(query, by)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public Collection<FilmDto> getCommonFilms(Long userId, Long friendId) {
        log.info("Get common films by user {} and friend {}", userId, friendId);
        userRepository.checkUserId(userId);
        userRepository.checkUserId(friendId);
        return filmRepository.getCommonFilms(userId, friendId)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }
}