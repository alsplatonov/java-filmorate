package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public Collection<GenreDto> findAll() {
        log.info("Получение списка жанров");
        return genreService.findAll();
    }

    @GetMapping("/{id}")
    public GenreDto findById(@PathVariable long id) {
        log.info("Получение жанра по id {}", id);
        return genreService.findGenreById(id);
    }
}
