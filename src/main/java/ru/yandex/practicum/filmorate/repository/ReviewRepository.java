package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

public interface ReviewRepository {
    Review getReviewsById(Long id);

    Review createReview(Review review);

    Review updateReview(Review review);
}
