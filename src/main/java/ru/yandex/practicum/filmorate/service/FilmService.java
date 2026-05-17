package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.LikesDbStorage;
import ru.yandex.practicum.filmorate.dao.MpaRatingDbStorage;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmDbStorage filmDbStorage;
    private final LikesDbStorage likesDbStorage;
    private final MpaRatingDbStorage mpaRatingDbStorage;
    private final GenreDbStorage genreDbStorage;

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public FilmDto create(NewFilmRequest request) {
        Film film = FilmMapper.mapToFilm(request);
        validateReleaseDate(film);
        if (request.getMpa() != null) {
            MpaRating mpa = mpaRatingDbStorage.findById(request.getMpa().getId())
                    .orElseThrow(() -> new NotFoundException("MPA не найден"));
            film.setMpa(mpa);
        }

        if (request.getGenres() != null && !request.getGenres().isEmpty()) {
            Set<Genre> genres = request.getGenres().stream()
                    .map(g -> genreDbStorage.findById(g.getId())
                            .orElseThrow(() -> new NotFoundException("Жанр не найден. ID: " + g.getId())))
                    .collect(Collectors.toSet());

            film.setGenres(genres);
        }

        film = filmDbStorage.create(film);
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto update(UpdateFilmRequest request) {
        // пробуем найти пользователя, если нет — выбросится NotFoundException
        Film updatedFilm = filmDbStorage.findById(request.getId())
                .map(film -> FilmMapper.updateFilmFields(film, request))
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        validateReleaseDate(updatedFilm);
        updatedFilm = filmDbStorage.update(updatedFilm);
        return FilmMapper.mapToFilmDto(updatedFilm);
    }

    public Collection<FilmDto> findAll() {
        return filmDbStorage.findAll().stream()
                .map(this::getFilmExtensions)
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto findById(Long id) {
        return filmDbStorage.findById(id)
                .map(this::getFilmExtensions)
                .map(FilmMapper::mapToFilmDto)
                .orElseThrow(() -> new NotFoundException("Фильм не найден с ID: " + id));
    }

    public void setLike(Long filmId, Long userId) {
        if (!likesDbStorage.isLiked(filmId, userId)) {
            likesDbStorage.addLike(filmId, userId);
        }
    }

    public void removeLike(Long filmId, Long userId) {
        likesDbStorage.removeLike(filmId, userId);
    }

    public Collection<FilmDto> getPopularFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("count должен быть больше 0");
        }

        return filmDbStorage.findAll().stream()
                .map(this::getFilmExtensions)
                // сортировка по лайкам
                .sorted((f1, f2) -> Integer.compare(
                        likesDbStorage.getLikesCount(f2.getId()),
                        likesDbStorage.getLikesCount(f1.getId())
                ))
                .limit(count)
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException(
                    "Дата релиза не может быть раньше 28 декабря 1895 года"
            );
        }
    }

    private Film getFilmExtensions(Film film) {
        // MPA
        if (film.getMpa() != null) {
            film.setMpa(
                    mpaRatingDbStorage.findById(film.getMpa().getId())
                            .orElseThrow(() -> new NotFoundException("MPA не найден"))
            );
        }
        // Genres
        if (film.getId() != null) {
            film.setGenres(genreDbStorage.findByFilmId(film.getId()));
        }
        return film;
    }
}
