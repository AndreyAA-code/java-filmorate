package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.DataBeginFilmEra;

import java.sql.Date;

@Data
public class User {
    private Long id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "[^\s]*")
    @DataBeginFilmEra
    private String login;
    private String name;
    @PastOrPresent
    @DataBeginFilmEra
    private Date birthday;
}
