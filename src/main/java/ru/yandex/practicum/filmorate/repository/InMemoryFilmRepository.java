package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor

public class InMemoryFilmRepository implements FilmRepository {

    private final HashMap<Long, Film> films = new HashMap<>();
    private final UserService userService;
    private final InMemoryMpaRepository mpaRepository;
    private final InMemoryGenreRepository genreRepository;

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film addFilm(Film film) {
        if (film.getMpa().getId() == null || !mpaRepository.mpaLevel.containsKey(film.getMpa().getId())) {
            throw new NotFoundException("MPA with id " + film.getMpa().getId() + " not found");
        }

        if (!(film.getGenres() == null)) {
            film.setGenres(film.getGenres()
                    .stream()
                    .map(genre -> Optional.ofNullable(genreRepository.genreMap.get(genre.getId()))
                            .orElseThrow(() -> new NotFoundException("Genre with id: " + genre.getId() + " not found")))
                    .sorted(Comparator.comparing(Genre::getId))
                    .collect(Collectors.toCollection(LinkedHashSet::new)));
        }

        film.setId(getNextId());
        film.setMpa(mpaRepository.mpaLevel.get(film.getMpa().getId()));

        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        checkIfFilmExists(newFilm.getId());

        if (!(newFilm.getGenres() == null)) {
            newFilm.setGenres(newFilm.getGenres()
                    .stream()
                    .map(genre -> Optional.ofNullable(genreRepository.genreMap.get(genre.getId()))
                            .orElseThrow(() -> new NotFoundException("Genre with id: " + genre.getId() + " not found")))
                    .sorted(Comparator.comparing(Genre::getId))
                    .collect(Collectors.toCollection(LinkedHashSet::new)));
        }

        Film oldFilm = films.get(newFilm.getId());
        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setDuration(newFilm.getDuration());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setMpa(mpaRepository.mpaLevel.get(newFilm.getMpa().getId()));
        return oldFilm;
    }

    @Override
    public Film getFilmById(Long id) {
        checkIfFilmExists(id);
        return films.get(id);
    }

    @Override
    public Film deleteFilmById(Long id) {
        checkIfFilmExists(id);
        return films.remove(id);
    }

    @Override
    public Film likeFilmById(Long id, Long userId) {
        checkIfFilmExists(id);
        films.get(id).getLikes().add(userId);
        return films.get(id);
    }

    @Override
    public Film deleteLikeUser(Long id, Long userId) {
        checkIfFilmExists(id);
        films.get(id).getLikes().remove(userService.getUserById(userId));
        return films.get(id);
    }

    public void checkIfFilmExists(Long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Film with id " + id + " not found");
        }
    }

    @Override
    public Collection<Film> getPopularFilms(Long count) {
        return films.values()
                .stream()
                .sorted(Comparator.comparing((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }



    public Long getNextId() {
        long maxID = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
        return ++maxID;
    }
}