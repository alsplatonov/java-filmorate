package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.directors.DirectorDbStorage;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.director.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorDbStorage directorDbStorage;


    public DirectorDto create(NewDirectorRequest request) {
        Director director = DirectorMapper.mapToDirector(request);
        director = directorDbStorage.create(director);
        return DirectorMapper.mapToDirectorDto(director);
    }

    public DirectorDto update(UpdateDirectorRequest request) {
        // пробуем найти режиссера, если нет — выбросится NotFoundException
        Director updatedDirector = directorDbStorage.findById(request.getId())
                .map(director -> DirectorMapper.updateDirectorFields(director, request))
                .orElseThrow(() -> new NotFoundException("Режиссёр не найден"));
        updatedDirector = directorDbStorage.update(updatedDirector);
        return DirectorMapper.mapToDirectorDto(updatedDirector);
    }

    public Collection<DirectorDto> findAll() {
        return directorDbStorage.findAll().stream()
                .map(DirectorMapper::mapToDirectorDto)
                .collect(Collectors.toList());
    }

    public DirectorDto findById(Long id) {
        return directorDbStorage.findById(id)
                .map(DirectorMapper::mapToDirectorDto)
                .orElseThrow(() -> new NotFoundException("Режиссёр не найден с ID: " + id));
    }

    public void removeDirector(Long id) {
        directorDbStorage.removeDirector(id);
    }
}
