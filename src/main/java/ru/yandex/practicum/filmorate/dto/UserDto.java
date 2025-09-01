package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Date;

@Data
public class UserDto {
    @JsonProperty (access = JsonProperty.Access.READ_ONLY)
    private long id;
    private String name;
    private String email;
    private Date birthday;
    private String login;
}
