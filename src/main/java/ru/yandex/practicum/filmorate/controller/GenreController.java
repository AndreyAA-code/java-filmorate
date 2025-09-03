package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/genres")

public class GenreController {
    private final FilmService filmService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Genre> getAllGenres() {
        return filmService.getAllGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable long id) {
        return filmService.getGenreById(id);
    }
}
