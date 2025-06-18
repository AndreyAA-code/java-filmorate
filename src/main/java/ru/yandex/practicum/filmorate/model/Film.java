package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.DataBeginFilmEra;

import java.sql.Date;

/**
 * Film.
 */
@Data
public class Film {
    private Long id;
    @NotBlank
    private String name;
    @Size(min = 0, max = 200)
    private String description;
//@ReleaseDateShouldBeAfterBeginningOfFilmEra
   @DataBeginFilmEra
    private Date releaseDate;
    @Min(0)
    private Integer duration;
}
