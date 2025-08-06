package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.DataBeginFilmEra;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

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
    @DataBeginFilmEra
    private Date releaseDate;
    @Min(0)
    private Integer duration;
    private Set<Long> likes = new HashSet<>();

    public Integer getLikesSize(){
        return likes.size();
    }

}
