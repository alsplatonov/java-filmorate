package ru.yandex.practicum.filmorate.dao.films;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    Collection<Film> findAll();

    Optional<Film> findById(Long id);

    Collection<Film> findRecommendations(Long userId);

    Film delete(Long filmId);

    List<Film> getFilmsByDirector(Long directorId, String sortBy);

    List<Film> getPopular(int count, Long genreId, Long year);

    List<Film> searchBy(String query, String by);
}

