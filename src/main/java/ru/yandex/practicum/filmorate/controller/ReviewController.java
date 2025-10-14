package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/reviews")

public class ReviewController {
    private final FilmService filmService;

    @GetMapping
    public List<Review> getReviews(@RequestParam Optional<Long> filmId,
                                   @RequestParam(defaultValue = "10") Long count) {
        return filmService.getReviews(filmId, count);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Long id) {
        return filmService.getReviewById(id);
    }

    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) {
        return filmService.createReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return filmService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        filmService.deleteReview(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Review likeReview(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.addLikeReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review dislikeReview(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.addDislikeReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public Review deleteDislikeReview(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.deleteDislikeReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Review deleteLikeReview(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.deleteLikeReview(id, userId);
    }

}
