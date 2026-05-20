package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaRatingDbStorage;
import ru.yandex.practicum.filmorate.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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

    public MpaRatingDto findById(long id) {
        return mpaRatingDbStorage.findById(id)
                .map(MpaRatingMapper::mapToRatingDto)
                .orElseThrow(() -> new NotFoundException("Mpa c id " + id + " не найден"));
    }
}
