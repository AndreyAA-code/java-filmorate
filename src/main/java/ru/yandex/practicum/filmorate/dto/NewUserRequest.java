package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

import java.sql.Date;

@Data
public class NewUserRequest {
    private String login;
    private String email;
    private String name;
    private Date birthday;
    private long id;
}
