package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Collection;
import java.util.Comparator;

@Service
public class FilmService {

    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
    private final Comparator<Film> likesComparator = Comparator.comparing(Film::getLikesSize);



    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }

    public Film addLike(Long id, Long userId) {
            getFilmById(id).getLikes().add(userId);
            log.info("Film: {} successfully got like with user id: {}", filmStorage.getFilms().get(id),userId);
            return filmStorage.getFilms().get(id);
    }

    public Film removeLike(Long id, Long userId) {
        if (getFilmById(id).getLikes().contains(userId)) {
            getFilmById(id).getLikes().remove(userId);
            log.info("Film: {} successfully removed like with user id: {}", filmStorage.getFilms().get(id),userId);
            return filmStorage.getFilms().get(id);
        }
         throw new NotFoundException("UserId not found");
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getFilms().values()
                .stream()
                .sorted(likesComparator.reversed())
                .limit(count)
                .toList();
    }
    public Film getFilmById(Long id) {
        if (filmStorage.getFilms().containsKey(id)) {
            return filmStorage.getFilms().get(id);
        }
        throw new NotFoundException("Film with id " + id + " not found");
    }
}
