package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.mpa.MpaRatingDbStorage;
import ru.yandex.practicum.filmorate.dto.mpa.MpaRatingDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.MpaRatingMapper;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaRatingDbStorage mpaRatingDbStorage;

    public Collection<MpaRatingDto> findAll() {
        return mpaRatingDbStorage.findAll().stream()
                .map(MpaRatingMapper::mapToRatingDto)
                .collect(Collectors.toList());
    }

    public MpaRatingDto findById(Long id) {
        if (id == null) {
            throw new ValidationException("id mpa не должен быть null");
        }
        return mpaRatingDbStorage.findById(id)
                .map(MpaRatingMapper::mapToRatingDto)
                .orElseThrow(() -> new NotFoundException("Mpa c id " + id + " не найден"));
    }
}
