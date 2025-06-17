package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.sql.Date;

@Data
public class User {
    private Long id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "[^\s]*")
    private String login;
    private String name;
    @PastOrPresent
    private Date birthday;
}
