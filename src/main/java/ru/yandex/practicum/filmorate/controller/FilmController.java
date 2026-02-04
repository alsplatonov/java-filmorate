package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Long, Film> films = new HashMap<>();
    private Long idCounter = 1L;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validateReleaseDate(film);
        film.setId(idCounter++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        validateReleaseDate(film);
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Фильм с id=" + film.getId() + " не найден");
        }
        films.put(film.getId(), film);
        log.info("Обновлён фильм: {}", film);
        return film;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException(
                    "Дата релиза не может быть раньше 28 декабря 1895 года"
            );
        }
    }
}
