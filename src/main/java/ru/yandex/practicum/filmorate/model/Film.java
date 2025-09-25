package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.DataBeginFilmEra;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Long id;
    @NotBlank
    private String name;
    @Size(max=200)
    private String description;
    @DataBeginFilmEra
    private LocalDate releaseDate;
    @Min(0)
    private Long duration;
    private Set<Long> likes = new HashSet<>();
    private Mpa mpa;
    private Set<Genre> genres;

}
