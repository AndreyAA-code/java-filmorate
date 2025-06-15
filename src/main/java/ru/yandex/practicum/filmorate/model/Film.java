package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.sql.Date;

/**
 * Film.
 */
@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private Date releaseDate;
    private Integer duration;
}
