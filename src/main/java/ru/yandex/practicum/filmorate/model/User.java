package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "[^\s]*")
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;
    @Builder.Default
    private Set<Long> friends = new HashSet<>();
}