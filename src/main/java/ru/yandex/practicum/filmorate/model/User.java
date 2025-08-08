package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@RequiredArgsConstructor

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
    private Set<Long> friends = new HashSet<>();
}
