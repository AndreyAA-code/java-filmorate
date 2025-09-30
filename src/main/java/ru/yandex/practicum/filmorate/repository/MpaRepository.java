package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Mpa;
import java.util.Collection;

public interface MpaRepository {

    Collection<Mpa> getMpas();

    Mpa getMpaById(Long id);

    void checkMpaId(Long id);
}
