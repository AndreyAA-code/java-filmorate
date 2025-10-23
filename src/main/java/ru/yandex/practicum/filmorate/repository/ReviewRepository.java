package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {

    Review getReviewsById(Long id);

    Review createReview(Review review);

    Review updateReview(Review review);

    void deleteReview(Long reviewId);

    List<Review> getReviews(Optional<Long> filmId, Long count);

    Review addLikeReview(Long reviewId, Long userId);

    Review addDislikeReview(Long reviewId, Long userId);

    Review deleteLikeReview(Long reviewId, Long userId);

    Review deleteDislikeReview(Long reviewId, Long userId);
}
