package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Value
@Builder
public class FilmDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private Mpa mpa;
    @Builder.Default
    private Set<Long> likes = new HashSet<>();
    @Builder.Default
    private Set<Genre> genres = new HashSet<>();

}