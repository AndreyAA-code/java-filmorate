package ru.yandex.practicum.filmorate.repository.DbRepositories;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.mappers.ReviewRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;

@Repository
@AllArgsConstructor
@Primary
public class DbReviewRepository implements ReviewRepository {

    private final JdbcTemplate jdbc;
    private final ReviewRowMapper mapper;

    private static final String FIND_REVIEW_BY_ID_QUERY = "SELECT * FROM reviews WHERE review_id = ?";
    private static final String CREATE_REVIEW_QUERY = "INSERT INTO reviews (content, isPositive, user_id, film_id) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_REVIEW_QUERY = "UPDATE reviews SET content = ?, isPositive = ?, user_id = ?, film_id = ? WHERE review_id =?";

    @Override
    public Review getReviewsById(Long id){
        Review review = jdbc.queryForObject(FIND_REVIEW_BY_ID_QUERY,mapper,id);
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
        return review;
    }

    @Override
    public Review updateReview(Review newReview) {
        jdbc.update(UPDATE_REVIEW_QUERY, newReview.getContent(), newReview.getIsPositive(), newReview.getUserId(),
                newReview.getFilmId(), newReview.getReviewId());
        return newReview;
    }

}
