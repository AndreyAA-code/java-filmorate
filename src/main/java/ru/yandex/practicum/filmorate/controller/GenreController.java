package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    public final FilmService filmService;

    @GetMapping
    public Collection<Genre> getGenres() {
        return filmService.getGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@Valid @PathVariable Long id) {
        return filmService.getGenresById(id);
    }
}
