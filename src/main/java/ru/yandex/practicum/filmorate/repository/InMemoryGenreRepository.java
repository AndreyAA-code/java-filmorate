package ru.yandex.practicum.filmorate.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryGenreRepository implements GenreRepository {

    final Map<Long, Genre> genreMap = Map.of(
            1L, new Genre(1L, "Комедия"),
            2L, new Genre(2L, "Драма"),
            3L, new Genre(3L, "Мультфильм"),
            4L, new Genre(4L, "Триллер"),
            5L, new Genre(5L, "Документальный"),
            6L, new Genre(6L, "Боевик")
    );
   @Override
    public Collection<Genre> getGenres() {
        return genreMap.values()
                .stream()
                .sorted(Comparator.comparing((Genre genre) -> genre.getId()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Genre getGenresById(Long id) {
        checkGenreId(id);
        return genreMap.get(id);
    }

    @Override
    public void checkGenreId(Long id) {
        if (!genreMap.containsKey(id)) {
            throw new NotFoundException("Genre with id " + id + " not found");
        }
    }
}
