package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;
import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage {
    Director create(Director director);

    Director update(Director director);

    Collection<Director> findAll();

    Optional<Director> findById(Long id);

    void removeDirector(Long userId);
}
