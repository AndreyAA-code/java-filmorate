package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Value
@Builder
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    @Builder.Default
    private Set<Long> friends = new HashSet<>();
}