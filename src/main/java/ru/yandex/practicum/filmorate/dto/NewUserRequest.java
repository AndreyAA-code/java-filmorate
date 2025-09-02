package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.sql.Date;

@Data
public class NewUserRequest {
    private String login;
    private String email;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private Date birthday;
    private long id;
}
