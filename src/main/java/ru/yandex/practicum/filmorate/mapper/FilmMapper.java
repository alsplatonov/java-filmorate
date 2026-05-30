package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.LinkedHashSet;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {
    public static Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        film.setMpa(MpaRatingMapper.mapToRating(request.getMpa()));
        film.setGenres(null);
        film.setDirector(null);
        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setMpa(MpaRatingMapper.mapToRatingDto(film.getMpa()));
        dto.setGenres(
                film.getGenres() == null ? null :
                        film.getGenres().stream()
                                .sorted(Comparator.comparing(Genre::getId))
                                .map(GenreMapper::mapToGenreDto)
                                .collect(Collectors.toCollection(LinkedHashSet::new))
        );
        dto.setDirectors(
                film.getDirector() == null ? new HashSet<>() :
                        film.getDirector().stream()
                                .sorted(Comparator.comparing(Director::getId))
                                .map(DirectorMapper::mapToDirectorDto)
                                .collect(Collectors.toCollection(LinkedHashSet::new))
        );
        return dto;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        if (request.hasName()) {
            film.setName(request.getName());
        }

        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }

        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }

        if (request.hasDuration()) {
            film.setDuration(request.getDuration());
        }

        if (request.hasDirectors()) {
            film.setDirector(
                    request.getDirector().stream()
                            .map(DirectorMapper::mapToDirector)
                            .collect(Collectors.toSet())
            );
        }

        //дополнить mpa Genres
        return film;
    }
}