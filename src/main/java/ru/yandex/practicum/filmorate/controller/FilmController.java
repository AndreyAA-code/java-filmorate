package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@AllArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {

    public final FilmService filmService;

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("{id}")
    public Film getFilmById(@Valid @PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updatFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/{id}")
    public Film deleteFilmById(@PathVariable Long id) {
        return filmService.deleteFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film likeFilmById(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.likeFilmById(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLikeUser(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.deleteLikeUser(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") Long count) {
        return filmService.getPopularFilms(count);
    }

}
