package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.genres.GenreDbStorage;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    public Collection<GenreDto> findAll() {
        return genreDbStorage.findAll().stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toList());
    }

    public GenreDto findGenreById(Long id) {
        if (id == null) {
            throw new ValidationException("id жанра не должен быть null");
        }
        return genreDbStorage.findById(id)
                .map((GenreMapper::mapToGenreDto))
                .orElseThrow(() -> new NotFoundException("Жанр не найден"));
    }
}
