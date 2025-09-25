package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@AllArgsConstructor
public class MpaController {
    FilmService filmService;

    @GetMapping
    public Collection<Mpa> getMpas() {
        return filmService.getMpas();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@Valid @PathVariable Long id) {
        return filmService.getMpaById(id);
    }
}
