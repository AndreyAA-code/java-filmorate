package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmRepository filmRepository;

    public List<FilmDto> findAll() {
        return filmRepository.findAll()
                .stream()
                .map(FilmMapper::mapToFilmDto)
         .collect(Collectors.toList());
    }
    public FilmDto createFilm(NewFilmRequest filmRequest) {
        if (filmRequest.getName() == null || filmRequest.getName().isEmpty()) {
            throw new ValidationException("Название должно быть указано");
        }

        Optional<Film> alreadyExistFilm = filmRepository.findByName(filmRequest.getName());
        if (alreadyExistFilm.isPresent()) {
            throw new ValidationException("Такой фильм уже есть");
        }

        Film film = FilmMapper.mapToFilm(filmRequest);
        film = filmRepository.save(film);
        return FilmMapper.mapToFilmDto(film);
    }


}
 /*   private final FilmStorage filmStorage;


    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }

    public Set<Long> addLike(Long id, Long userId) {
        return filmStorage.addLike(id, userId);
    }

    public Film removeLike(Long id, Long userId) {
        return filmStorage.removeLike(id, userId);
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

} */
