package ru.yandex.practicum.filmorate.repository.DbRepositories;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Repository
@AllArgsConstructor
@Primary
public class DbReviewRepository implements ReviewRepository {
    private final JdbcTemplate jdbc;
    private final ReviewRowMapper mapper;
    DbFeedRepository dbFeedRepository;

    private static final String FIND_REVIEW_BY_ID_QUERY = "SELECT * FROM reviews WHERE review_id = ?";
    private static final String FIND_USERID_FROM_REVIEW_QUERY = "SELECT user_id FROM reviews WHERE review_id = ?";
    private static final String CREATE_REVIEW_QUERY = "INSERT INTO reviews (content, isPositive, user_id, film_id) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_REVIEW_QUERY = "UPDATE reviews SET content = ?, isPositive = ? WHERE review_id =?";
    private static final String DELETE_REVIEW_QUERY = "DELETE FROM reviews WHERE review_id = ?";
    private static final String GET_REVIEWS_BY_FILM_ID_QUERY = "SELECT reviews.*, COALESCE(SUM(reviews_users.useful),0) AS useful_sum\n" +
            "FROM reviews LEFT JOIN reviews_users ON reviews.review_id = reviews_users.review_id\n" +
            "WHERE film_id =? GROUP BY reviews.review_id ORDER BY useful_sum DESC LIMIT ?";
    private static final String GET_REVIEWS_FOR_ALL_FILMS = "SELECT reviews.*, COALESCE(SUM(reviews_users.useful),0) AS useful_sum\n" +
            "FROM reviews LEFT JOIN reviews_users ON reviews.review_id = reviews_users.review_id\n" +
            "GROUP BY reviews.review_id ORDER BY useful_sum DESC LIMIT ?";
    private static final String IF_REVIEW_EXISTS_QUERY = "SELECT COUNT(*) FROM reviews WHERE review_id = ?";
    private static final String ADD_LIKE_DISLIKE_TO_REVIEW_QUERY = "INSERT INTO reviews_users (review_id, user_id, useful) VALUES (?, ?, ?)";
    private static final String DELETE_LIKE_DISLIKE_FOR_REVIEW_QUERY  = "DELETE FROM reviews_users WHERE review_id = ? AND user_id = ?";
    private static final String GET_USEFUL_FOR_REVIEW = "SELECT SUM(useful) FROM reviews_users WHERE review_id = ?";
    private static final String DELETE_ALL_LIKES_DISLIKES_FOR_REVIEW = "DELETE FROM reviews_users WHERE review_id = ?";
    private static final String IF_LIKE_DISLIKE_EXISTS_QUERY = "SELECT COUNT(*) FROM reviews_users WHERE review_id = ? AND user_id = ?";
    private static final String UPDATE_LIKE_DISLIKE_QUERY = "UPDATE reviews_users SET useful = ? WHERE review_id =? AND user_id = ?";

    @Override
    public Review getReviewsById(Long id) {
        log.info("Get reviews by id: {}", id);
        checkReviewId(id);
        Review review = jdbc.queryForObject(FIND_REVIEW_BY_ID_QUERY, mapper, id);

        return review;
    }

    @Override
    public Review createReview(Review review) {
        log.info("Create review: {}", review);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_REVIEW_QUERY, new String[]{"review_id"});
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            return ps;
        }, keyHolder);
        log.info("Created review: {}", keyHolder.getKey());
        Long generatedId = keyHolder.getKey().longValue();
        log.info("Generated review id: {}", generatedId);
        review.setReviewId(generatedId);
        log.info("Created review: {}", review);
        review.setUseful(0L);
        dbFeedRepository.createUserEvent(review.getUserId(),review.getReviewId(),REVIEW,ADD);
        log.info("Created review: {}", review);
        return review;
    }

    @Override
    public Review updateReview(Review newReview) {
        log.info("Update review: {}", newReview);
        jdbc.update(UPDATE_REVIEW_QUERY, newReview.getContent(), newReview.getIsPositive(), newReview.getReviewId());
        jdbc.update(DELETE_ALL_LIKES_DISLIKES_FOR_REVIEW, newReview.getReviewId());
        Long reviewUserId = jdbc.queryForObject(FIND_USERID_FROM_REVIEW_QUERY, Long.class, newReview.getReviewId());
        dbFeedRepository.createUserEvent(reviewUserId,newReview.getReviewId(),REVIEW,UPDATE);
        Review review = jdbc.queryForObject(FIND_REVIEW_BY_ID_QUERY, mapper, newReview.getReviewId());
        return review;
    }

    @Override
    public void deleteReview(Long reviewId) {
        log.info("Delete review: {}", reviewId);
        checkReviewId(reviewId);
        Review review = jdbc.queryForObject(FIND_REVIEW_BY_ID_QUERY, mapper, reviewId);
        dbFeedRepository.createUserEvent(review.getUserId(),review.getReviewId(),REVIEW,REMOVE);
        jdbc.update(DELETE_REVIEW_QUERY, reviewId);
        jdbc.update(DELETE_ALL_LIKES_DISLIKES_FOR_REVIEW, reviewId);
        log.info("Deleted review: {}", reviewId);
    }

    @Override
    public List<Review> getReviews(Optional<Long> filmId, Long count) {
        log.info("Get reviews by filmId: {}, count: {}", filmId, count);
        List<Review> reviews;

        if (filmId.isPresent()) {
            reviews = jdbc.query(GET_REVIEWS_BY_FILM_ID_QUERY, mapper, filmId.get(), count);
        } else {
            reviews = jdbc.query(GET_REVIEWS_FOR_ALL_FILMS, mapper, count);
            log.info("Get reviews for all filmId: {}", filmId);
        }
        for (Review review : reviews) {
            review.setUseful(jdbc.queryForObject(GET_USEFUL_FOR_REVIEW,Long.class,review.getReviewId()));
            if (review.getUseful() == null) {
                review.setUseful(0L);
                log.info("Get reviews for useful: {}", review.getReviewId());
            }
        }
        log.info("Get reviews by filmId: {}, count: {}", filmId, count);
        return reviews;
    }

    @Override
    public Review addLikeReview(Long reviewId, Long userId) {
        log.info("Add like review: {}", reviewId);
        checkReviewId(reviewId);
        if (!checkIfLikeOrDislikeExists(reviewId, userId)) {
            jdbc.update(ADD_LIKE_DISLIKE_TO_REVIEW_QUERY, reviewId, userId, 1);
        } else {
            jdbc.update(UPDATE_LIKE_DISLIKE_QUERY, 1, reviewId, userId);
        }
        Review review = getReviewsById(reviewId);
        review.setUseful(jdbc.queryForObject(GET_USEFUL_FOR_REVIEW,Long.class,reviewId));
        log.info("Add like review: {}", reviewId);
        return review;
    }

    private boolean checkIfLikeOrDislikeExists(Long reviewId, Long userId) {
        log.info("Check if like or dislike exists for review: {}", reviewId);
        return jdbc.queryForObject(IF_LIKE_DISLIKE_EXISTS_QUERY, Boolean.class, reviewId, userId);
    }

    @Override
    public Review addDislikeReview(Long reviewId, Long userId) {
        log.info("Add dislike review: {}", reviewId);
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
        log.info("Delete like review: {}", reviewId);
        jdbc.update(DELETE_LIKE_DISLIKE_FOR_REVIEW_QUERY, reviewId, userId);
        Review review = getReviewsById(reviewId);
        review.setUseful(jdbc.queryForObject(GET_USEFUL_FOR_REVIEW,Long.class,reviewId));
        if (review.getUseful() == null) {
            review.setUseful(0L);
        }
        log.info("Delete like review: {}", reviewId);
        return review;
    }

    @Override
    public Review deleteDislikeReview(Long reviewId, Long userId) {
        log.info("Delete dislike review: {}", reviewId);
        checkReviewId(reviewId);
        Review review = getReviewsById(reviewId);
        review.setUseful(jdbc.queryForObject(GET_USEFUL_FOR_REVIEW,Long.class,reviewId));
        return review;
    }

    private void checkReviewId(Long reviewId) {
        log.info("Check review id {}", reviewId);
        if (jdbc.queryForObject(IF_REVIEW_EXISTS_QUERY, Integer.class, reviewId) == 0) {
            throw new NotFoundException("Review with id " + reviewId + " not found");
        }
    }
}
