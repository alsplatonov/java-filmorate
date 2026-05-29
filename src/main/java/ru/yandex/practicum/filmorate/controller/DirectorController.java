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
        return directorService.findAll();
    }

    @GetMapping("/{id}")
    public DirectorDto findById(@PathVariable Long id) {
        return directorService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void removeDirector(@PathVariable Long id) {
        directorService.removeDirector(id);
    }
}
