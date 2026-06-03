package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.event.EventStorage;
import ru.yandex.practicum.filmorate.dao.directors.DirectorDbStorage;
import ru.yandex.practicum.filmorate.dao.films.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.genres.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.likes.LikesDbStorage;
import ru.yandex.practicum.filmorate.dao.mpa.MpaRatingDbStorage;
import ru.yandex.practicum.filmorate.dao.users.UserDbStorage;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final LikesDbStorage likesDbStorage;
    private final MpaRatingDbStorage mpaRatingDbStorage;
    private final GenreDbStorage genreDbStorage;

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final DirectorDbStorage directorDbStorage;
    private final EventStorage eventStorage;

    public FilmDto create(NewFilmRequest request) {
        Film film = FilmMapper.mapToFilm(request);
        validateReleaseDate(film);
        //mpa
        if (request.getMpa() != null) {
            MpaRating mpa = mpaRatingDbStorage.findById(request.getMpa().getId())
                    .orElseThrow(() -> new NotFoundException("MPA не найден"));
            film.setMpa(mpa);
        }
        //genres
        if (request.getGenres() != null && !request.getGenres().isEmpty()) {

            Set<Long> genreIds = request.getGenres().stream()
                    .map(GenreDto::getId)
                    .collect(Collectors.toSet());

            Set<Genre> genres = resolveEntities(
                    genreIds,
                    genreDbStorage::findGenresByIds,
                    Genre::getId,
                    "Жанры не найдены: "
            );

            film.setGenres(genres);
        }
        //directors
        if (request.getDirector() != null && !request.getDirector().isEmpty()) {

            Set<Long> directorIds = request.getDirector().stream()
                    .map(DirectorDto::getId)
                    .collect(Collectors.toSet());

            Set<Director> directors = resolveEntities(
                    directorIds,
                    directorDbStorage::findDirectorsByIds,
                    Director::getId,
                    "Режиссёры не найдены: "
            );

            film.setDirectors(directors);
        }

        film = filmDbStorage.create(film);
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto update(UpdateFilmRequest request) {
        // пробуем найти фильм, если нет — выбросится NotFoundException
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

    public Collection<FilmDto> searchBy(String query, String by) {
        System.out.println("searchBy: " + query);
        List<Film> films = filmDbStorage.searchBy(query, by);
        return films.stream()
                .map(this::getFilmExtensions)
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto findById(Long id) {
        if (id == null) {
            throw new ValidationException("id фильма не должен быть null");
        }
        return filmDbStorage.findById(id)
                .map(this::getFilmExtensions)
                .map(FilmMapper::mapToFilmDto)
                .orElseThrow(() -> new NotFoundException("Фильм не найден с ID: " + id));
    }

    public void setLike(Long filmId, Long userId) {
        if (filmId == null || userId == null) {
            throw new ValidationException("id фильма или пользователя не должен быть null");
        }
        userDbStorage.findById(userId)
                .orElseThrow(() -> {
                    throw new NotFoundException(format("Пользователь с id %d не найден.\n", userId));
                });
        filmDbStorage.findById(filmId)
                .orElseThrow(() -> {
                    throw new NotFoundException(format("Фильм с id %d не найден для обновления\n", filmId));
                });
        if (!likesDbStorage.isLiked(filmId, userId)) {
            likesDbStorage.addLike(filmId, userId);
        }

        Event event = new Event();
        event.setTimestamp(System.currentTimeMillis());
        event.setUserId(userId);
        event.setEventType(EventType.LIKE);
        event.setOperation(Operation.ADD);
        event.setEntityId(filmId);

        eventStorage.create(event);
    }

    public void removeLike(Long filmId, Long userId) {
        if (filmId == null || userId == null) {
            throw new ValidationException("id фильма или пользователя не должен быть null");
        }
        userDbStorage.findById(userId)
                .orElseThrow(() -> {
                    throw new NotFoundException(format("Пользователь с id %d не найден.\n", userId));
                });
        filmDbStorage.findById(filmId)
                .orElseThrow(() -> {
                    throw new NotFoundException(format("Фильм с id %d не найден для обновления\n", filmId));
                });
        likesDbStorage.removeLike(filmId, userId);

        Event event = new Event();
        event.setTimestamp(System.currentTimeMillis());
        event.setUserId(userId);
        event.setEventType(EventType.LIKE);
        event.setOperation(Operation.REMOVE);
        event.setEntityId(filmId);

        eventStorage.create(event);
    }

    public Collection<FilmDto> getPopular(int count, Long genreId, Long year) {
        if (count <= 0) {
            throw new ValidationException("count должен быть больше 0");
        }

        // В базе реализованна сортировка и count
        List<Film> popularFilms = filmDbStorage.getPopular(count, genreId, year);

        return popularFilms.stream()
                .map(this::getFilmExtensions)
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public List<FilmDto> findRecommendationFilms(Long userId) {
        if (userId == null) {
            throw new ValidationException("id пользователя не может быть null");
        }
        userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return filmDbStorage.findRecommendations(userId).stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public List<FilmDto> getFilmsByDirector(Long directorId, String sortBy) {
        if (directorId == null) {
            throw new ValidationException("id режиссёра не должен быть null");
        }
        // проверка, что режиссёр существует
        Director director = directorDbStorage.findById(directorId)
                .orElseThrow(() -> new NotFoundException("Режиссёр не найден"));

        List<Film> films = filmDbStorage.getFilmsByDirector(director.getId(), sortBy);

        return films.stream()
                .map(this::getFilmExtensions)
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public Collection<FilmDto> getCommonFilms(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            throw new ValidationException("id полльзователя или друга не может быть null");
        }
        userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        userDbStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Друг не найден"));

        Collection<FilmDto> films = findAll();

        // поллучаем множество фильмов которые лайкнул друг
        Set<Long> friendLikedFilmsIds = films.stream()
                .map(FilmDto::getId)
                .filter(id -> likesDbStorage.isLiked(id, friendId))
                .collect(Collectors.toSet());

        // находим пересечения с множеством фильмов которые лайкнул юзер
        Set<FilmDto> commonLikedFilms = films.stream()
                .filter(film -> likesDbStorage.isLiked(film.getId(), userId))
                .filter(film -> friendLikedFilmsIds.contains(film.getId()))
                .collect(Collectors.toSet());

        // возвращаем популярные фильмы, но мы отсеяли те, которые не входят в список общих лайкнутых фильмов
        return getPopularFilms().stream()
                .filter(commonLikedFilms::contains)
                .collect(Collectors.toSet());
    }

    public FilmDto delete(Long filmId) {
        if (filmId == null) {
            throw new ValidationException("ID фильма не может быть null.");
        }
        filmDbStorage.findById(filmId)
                .orElseThrow(() -> {
                    throw new NotFoundException(String.format("Фильм с id %d не найден для удаления\n", filmId));
                });
        return FilmMapper.mapToFilmDto(filmDbStorage.delete(filmId));
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
        // Directors
        if (film.getDirectors() != null) {
            if (film.getId() != null) {
                film.setDirectors(
                        directorDbStorage.findByFilmId(film.getId())
                );
            }
        }
        return film;
    }

    private <T, K> Set<T> resolveEntities(
            Set<K> ids,
            Function<Set<K>, Collection<T>> finder,
            Function<T, K> idExtractor,
            String errorMessage
    ) {
        Set<T> entities = new HashSet<>(finder.apply(ids));

        Set<K> foundIds = entities.stream()
                .map(idExtractor)
                .collect(Collectors.toSet());

        Set<K> missingIds = new HashSet<>(ids);
        missingIds.removeAll(foundIds);

        if (!missingIds.isEmpty()) {
            throw new NotFoundException(errorMessage + missingIds);
        }

        return entities;
    }

    private Collection<FilmDto> getPopularFilms() {
        List<Film> films = filmDbStorage.findAll();

        Map<Long, Integer> likesMap = likesDbStorage.getLikesCountForFilms(
                films.stream()
                        .map(Film::getId)
                        .collect(Collectors.toList())
        );

        return films.stream()
                .map(this::getFilmExtensions)
                .sorted((f1, f2) -> Integer.compare(
                        likesMap.getOrDefault(f2.getId(), 0),
                        likesMap.getOrDefault(f1.getId(), 0)
                ))
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }
}
