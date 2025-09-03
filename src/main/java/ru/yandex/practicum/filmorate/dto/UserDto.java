package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserDto {
    private long id;
    private String name;
    private String email;
    private Date birthday;
    private String login;
    private final Set<Long> genres = new HashSet<>();
}
