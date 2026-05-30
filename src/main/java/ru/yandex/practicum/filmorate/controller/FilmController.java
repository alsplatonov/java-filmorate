package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.service.FilmService;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto create(@Valid @RequestBody NewFilmRequest film) {
        FilmDto newFilm = filmService.create(film);
        log.info("Добавлен фильм: {}", newFilm);
        return newFilm;
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody UpdateFilmRequest film) {
        FilmDto updFilm = filmService.update(film);
        log.info("Обновлён фильм: {}", updFilm);
        return updFilm;
    }

    @GetMapping
    public Collection<FilmDto> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public FilmDto findById(@PathVariable Long id) {
        return filmService.findById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.setLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> findPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }

    @GetMapping("/common")
    public Collection<FilmDto> getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        log.info("Пользователь {} запросил общие фильмы с {}", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }
}
