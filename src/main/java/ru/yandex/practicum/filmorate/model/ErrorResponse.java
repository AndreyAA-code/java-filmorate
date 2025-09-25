package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

}
