package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.service.DirectorService;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectorDto create(@Valid @RequestBody NewDirectorRequest director) {
        DirectorDto newDirector = directorService.create(director);
        log.info("Создан режиссёр: {}", newDirector);
        return newDirector;
    }

    @PutMapping
    public DirectorDto update(@Valid @RequestBody UpdateDirectorRequest request) {
        DirectorDto updatedDirector = directorService.update(request);
        log.info("Обновлён режиссёр: {}", updatedDirector);
        return updatedDirector;
    }

    @GetMapping
    public Collection<DirectorDto> findAll() {
        log.info("Запрос на получение всех режиссёров");

        Collection<DirectorDto> directors = directorService.findAll();

        log.info("Получено режиссёров: {}", directors.size());

        return directors;
    }

    @GetMapping("/{id}")
    public DirectorDto findById(@PathVariable Long id) {
        log.info("Запрос режиссёра с id={}", id);

        DirectorDto director = directorService.findById(id);

        log.info("Найден режиссёр: {}", director);

        return director;
    }

    @DeleteMapping("/{id}")
    public void removeDirector(@PathVariable Long id) {
        log.info("Запрос на удаление режиссёра с id={}", id);

        directorService.removeDirector(id);

        log.info("Режиссёр с id={} успешно удалён", id);
    }
}
