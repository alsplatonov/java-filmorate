package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.mpa.MpaRatingDto;
import ru.yandex.practicum.filmorate.service.MpaService;
import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<MpaRatingDto> findAll() {
        log.info("Получение списка mpa");
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    public MpaRatingDto findById(@PathVariable long id) {
        log.info("Получение mpa по id {}", id);
        return mpaService.findById(id);
    }
}
