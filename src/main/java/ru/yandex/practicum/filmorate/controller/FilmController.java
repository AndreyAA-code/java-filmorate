package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@AllArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {

    public final FilmService filmService;

    @GetMapping
    public Collection<FilmDto> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("{id}")
    public FilmDto getFilmById(@Valid @PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @PostMapping
    public FilmDto addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/{id}")
    public FilmDto deleteFilmById(@PathVariable Long id) {
        return filmService.deleteFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public FilmDto likeFilmById(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.likeFilmById(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public FilmDto deleteLikeUser(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.deleteLikeUser(id, userId);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getPopularFilms(@RequestParam(defaultValue = "10") Long count) {
        return filmService.getPopularFilms(count);
    }

}
