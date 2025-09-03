package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final FilmService filmService;

    @GetMapping
    public List<Mpa> getAllMpa() {
        return filmService.getAllMpa();
    }
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mpa findMpaById(@PathVariable long id) {
        return filmService.getMpaById(id);
    }
}
