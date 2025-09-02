package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.sql.Date;

@Data
public class UpdateUserRequest {
    private String login;
    private String email;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private Date birthday;
    private long id;

    public boolean hasLogin() {
        return ! (login == null || login.isBlank());
    }

    public boolean hasEmail() {
        return ! (email == null || email.isBlank());
    }

    public boolean hasBirthday() {
        return ! (birthday == null);
    }

    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }
}
