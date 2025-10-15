package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Review {
    private Long reviewId;
    @Size(max = 200)
    @NotBlank
    @NotNull
    private String content;
    private Boolean isPositive;
    private Long useful = 0L;
    @NotNull
    private Long userId;
    @NotNull
    private Long filmId;
}
