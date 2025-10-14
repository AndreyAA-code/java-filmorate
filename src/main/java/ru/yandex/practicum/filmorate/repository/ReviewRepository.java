package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    Review getReviewsById(Long id);

    Review createReview(Review review);

    Review updateReview(Review review);

    void deleteReview(Long reviewId);

    List<Review> getReviews(Optional<Long> filmId, Long count);

}
