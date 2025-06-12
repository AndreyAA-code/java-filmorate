package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.time.Duration;

/**
 * Film.
 */
@Data
public class Film {
    private long id;
    private String name;
    private String description;
    private Date releaseDate;
    private Duration duration;
}
