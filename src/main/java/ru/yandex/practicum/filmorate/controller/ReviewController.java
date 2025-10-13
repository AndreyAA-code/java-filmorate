package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FilmService;

@RestController
@AllArgsConstructor
@RequestMapping("/reviews")

public class ReviewController {
    private final FilmService filmService;

    @PostMapping
    public Review createReview(@RequestBody Review review) {
        return filmService.createReview(review);
    }


  /*  p
    POST /reviews

    Добавление нового отзыва.

            PUT /reviews

    Редактирование уже имеющегося отзыва.

    DELETE /reviews/{id}

    Удаление уже имеющегося отзыва.

    GET /reviews/{id}

    Получение отзыва по идентификатору.

    GET /reviews?filmId={filmId}&count={count}
    Получение всех отзывов по идентификатору фильма, если фильм не указан то все. Если кол-во не указано то 10.

    PUT /reviews/{id}/like/{userId} — пользователь ставит лайк отзыву.
    PUT /reviews/{id}/dislike/{userId} — пользователь ставит дизлайк отзыву.
    DELETE /reviews/{id}/like/{userId} — пользователь удаляет лайк/дизлайк отзыву.
    DELETE /reviews/{id}/dislike/{userId} — пользователь удаляет дизлайк отзыву.
*/
}
