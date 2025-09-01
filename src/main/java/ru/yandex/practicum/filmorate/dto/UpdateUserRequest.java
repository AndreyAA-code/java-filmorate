package ru.yandex.practicum.filmorate.dto;

import java.util.Date;

public class UpdateUserRequest {
    private String login;
    private String email;
    private String name;
    private Date birthday;

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
