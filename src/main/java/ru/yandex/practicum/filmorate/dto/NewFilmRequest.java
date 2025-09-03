package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.DataBeginFilmEra;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Data
public class NewFilmRequest {
    private Long id;
    @NotBlank
    private String name;
    @Size(min = 0, max = 200)
    private String description;
    @DataBeginFilmEra
    private Date releaseDate;
    @Min(0)
    private Integer duration;
    private Integer mpa;
    private final Set<Long> genres = new HashSet<>();
}
