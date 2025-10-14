package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Review {
    private Long id;
    @Size(max = 200)
    private String content;
    private Boolean isPositive;
    private Long useful;
    private Long userId;
    private Long filmId;

}
