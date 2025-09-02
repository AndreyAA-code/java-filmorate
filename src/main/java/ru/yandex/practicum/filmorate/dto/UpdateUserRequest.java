package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.sql.Date;

@Data
public class UpdateUserRequest {
    private String login;
    private String email;
    private String name;
    private Date birthday;
    @JsonIgnore // Игнорировать поле id из JSON
    private Long id;

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
