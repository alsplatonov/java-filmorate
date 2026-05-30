package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    Collection<Film> findAll();

    Optional<Film> findById(Long id);

    List<Film> getFilmsByDirector(Long directorId, String sortBy);
}

