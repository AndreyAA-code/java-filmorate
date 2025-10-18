package ru.yandex.practicum.filmorate.repository.DbRepositories;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.mappers.ReviewRowMapper;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.model.EventType.REVIEW;
import static ru.yandex.practicum.filmorate.model.Operation.*;

@Repository
@AllArgsConstructor
@Primary
public class DbReviewRepository implements ReviewRepository {
    private final JdbcTemplate jdbc;
    private final ReviewRowMapper mapper;
    DbFeedRepository dbFeedRepository;

    private static final String FIND_REVIEW_BY_ID_QUERY = "SELECT * FROM reviews WHERE review_id = ?";
    private static final String CREATE_REVIEW_QUERY = "INSERT INTO reviews (content, isPositive, user_id, film_id) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_REVIEW_QUERY = "UPDATE reviews SET content = ?, isPositive = ?, user_id = ?, film_id = ? WHERE review_id =?";
    private static final String DELETE_REVIEW_QUERY = "DELETE FROM reviews WHERE review_id = ?";
    private static final String GET_REVIEWS_BY_FILM_ID_QUERY = "SELECT * FROM reviews WHERE film_id = ? LIMIT ?";
    private static final String GET_REVIEWS_FOR_ALL_FILMS = "SELECT * FROM reviews WHERE review_id = ? LIMIT ?";
    private static final String IF_REVIEW_EXISTS_QUERY = "SELECT COUNT(*) FROM reviews WHERE review_id = ?";
    private static final String ADD_LIKE_DISLIKE_TO_REVIEW_QUERY = "INSERT INTO reviews_users (review_id, user_id, useful) VALUES (?, ?, ?)";
    private static final String DELETE_LIKE_DISLIKE_FOR_REVIEW_QUERY  = "DELETE FROM reviews_users WHERE review_id = ? AND user_id = ?";
    private static final String GET_USEFUL_FOR_REVIEW = "SELECT SUM(useful) FROM reviews_users WHERE review_id = ?";
    private static final String DELETE_ALL_LIKES_DISLIKES_FOR_REVIEW = "DELETE FROM reviews_users WHERE review_id = ?";
    private static final String IF_LIKE_DISLIKE_EXISTS_QUERY = "SELECT COUNT(*) FROM reviews_users WHERE review_id = ? AND user_id = ?";
    private static final String UPDATE_LIKE_DISLIKE_QUERY = "UPDATE reviews_users SET useful = ? WHERE review_id =? AND user_id = ?";

    @Override
    public Review getReviewsById(Long id) {
        checkReviewId(id);
        Review review = jdbc.queryForObject(FIND_REVIEW_BY_ID_QUERY, mapper, id);

        return review;
    }

    @Override
    public Review createReview(Review review) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_REVIEW_QUERY, new String[]{"review_id"});
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            return ps;
        }, keyHolder);
        Long generatedId = keyHolder.getKey().longValue();
        review.setReviewId(generatedId);
        review.setUseful(0L);
        dbFeedRepository.createUserEvent(review.getUserId(),review.getReviewId(),REVIEW,ADD);
        return review;
    }

    @Override
    public Review updateReview(Review newReview) {
        jdbc.update(UPDATE_REVIEW_QUERY, newReview.getContent(), newReview.getIsPositive(), newReview.getUserId(),
                newReview.getFilmId(), newReview.getReviewId());
        jdbc.update(DELETE_ALL_LIKES_DISLIKES_FOR_REVIEW, newReview.getReviewId());
        dbFeedRepository.createUserEvent(newReview.getUserId(),newReview.getReviewId(),REVIEW,UPDATE);
        return newReview;
    }

    @Override
    public void deleteReview(Long reviewId) {
        checkReviewId(reviewId);
        Review review = jdbc.queryForObject(FIND_REVIEW_BY_ID_QUERY, mapper, reviewId);
        dbFeedRepository.createUserEvent(review.getUserId(),review.getReviewId(),REVIEW,REMOVE);
        jdbc.update(DELETE_REVIEW_QUERY, reviewId);
        jdbc.update(DELETE_ALL_LIKES_DISLIKES_FOR_REVIEW, reviewId);
    }

    @Override
    public List<Review> getReviews(Optional<Long> filmId, Long count) {
        List<Review> reviews;

        if (filmId.isPresent()) {
            reviews = jdbc.query(GET_REVIEWS_BY_FILM_ID_QUERY, mapper, filmId.get(), count);
        } else {
            reviews = jdbc.query(GET_REVIEWS_FOR_ALL_FILMS, mapper, count);
        }
        for (Review review : reviews) {
            review.setUseful(jdbc.queryForObject(GET_USEFUL_FOR_REVIEW,Long.class,review.getReviewId()));
            if (review.getUseful() == null) {
                review.setUseful(0L);
            }
        }
        return reviews;
    }

    @Override
    public Review addLikeReview(Long reviewId, Long userId) {
        checkReviewId(reviewId);
        if (!checkIfLikeOrDislikeExists(reviewId, userId)) {
            jdbc.update(ADD_LIKE_DISLIKE_TO_REVIEW_QUERY, reviewId, userId, 1);
        } else {
            jdbc.update(UPDATE_LIKE_DISLIKE_QUERY, 1, reviewId, userId);
        }
        Review review = getReviewsById(reviewId);
        review.setUseful(jdbc.queryForObject(GET_USEFUL_FOR_REVIEW,Long.class,reviewId));
        return review;
    }

    private boolean checkIfLikeOrDislikeExists(Long reviewId, Long userId) {
        return jdbc.queryForObject(IF_LIKE_DISLIKE_EXISTS_QUERY, Boolean.class, reviewId, userId);
    }

    @Override
    public Review addDislikeReview(Long reviewId, Long userId) {
        checkReviewId(reviewId);
        if (!checkIfLikeOrDislikeExists(reviewId, userId)) {
            jdbc.update(ADD_LIKE_DISLIKE_TO_REVIEW_QUERY, reviewId, userId, -1);
        } else {
            jdbc.update(UPDATE_LIKE_DISLIKE_QUERY, -1, reviewId, userId);
        }

        Review review = getReviewsById(reviewId);
        review.setUseful(jdbc.queryForObject(GET_USEFUL_FOR_REVIEW,Long.class,reviewId));
        return review;
    }

    @Override
    public Review deleteLikeReview(Long reviewId, Long userId) {
        jdbc.update(DELETE_LIKE_DISLIKE_FOR_REVIEW_QUERY, reviewId, userId);
        Review review = getReviewsById(reviewId);
        review.setUseful(jdbc.queryForObject(GET_USEFUL_FOR_REVIEW,Long.class,reviewId));
        if (review.getUseful() == null) {
            review.setUseful(0L);
        }
        return review;
    }

    @Override
    public Review deleteDislikeReview(Long reviewId, Long userId) {
        checkReviewId(reviewId);
        Review review = getReviewsById(reviewId);
        review.setUseful(jdbc.queryForObject(GET_USEFUL_FOR_REVIEW,Long.class,reviewId));
        return review;
    }

    private void checkReviewId(Long reviewId) {
        if (jdbc.queryForObject(IF_REVIEW_EXISTS_QUERY, Integer.class, reviewId) == 0) {
            throw new NotFoundException("Review with id " + reviewId + " not found");
        }
    }
}
