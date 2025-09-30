package ru.yandex.practicum.filmorate.repository.InMemoryRepositories;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.MpaRepository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryMpaRepository implements MpaRepository {

    final Map<Long, Mpa> mpaLevel = Map.of(
            1L, new Mpa(1L, "G"),
            2L, new Mpa(2L, "PG"),
            3L, new Mpa(3L, "PG-13"),
            4L, new Mpa(4L, "R"),
            5L, new Mpa(5L, "NC-17")
    );

    @Override
    public Collection<Mpa> getMpas() {
        return mpaLevel.values()
                .stream()
                .sorted(Comparator.comparing((Mpa mpa) -> mpa.getId()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Mpa getMpaById(Long id) {
        checkMpaId(id);
        return mpaLevel.get(id);
    }

    @Override
    public void checkMpaId(Long id) {
        if (!mpaLevel.containsKey(id)) {
            throw new NotFoundException("MPA with id " + id + " not found");
        }
    }

}
