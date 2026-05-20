package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {
    private static final String FIND_ALL_GENRES = "SELECT * FROM genres ORDER BY id ASC";
    private static final String FIND_BY_GENRE_ID = "SELECT * FROM genres WHERE id = ?";
    private static final String FIND_GENRES_BY_IDS = "SELECT * FROM genres WHERE id IN (%s)";
    private static final String FIND_GENRE_BY_FILM_ID =
            "SELECT g.id, g.name " +
                    "FROM film_genres fg " +
                    "JOIN genres g ON fg.genre_id = g.id " +
                    "WHERE fg.film_id = ? " +
                    "ORDER BY g.id";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Genre> findAll() {
        return findMany(FIND_ALL_GENRES);
    }

    @Override
    public Optional<Genre> findById(long id) {
        return findOne(FIND_BY_GENRE_ID, id);
    }

    public Set<Genre> findByFilmId(long filmId) {
        return new LinkedHashSet<>(
                findMany(FIND_GENRE_BY_FILM_ID, filmId)
        );
    }

    public List<Genre> findGenresByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        String placeholders = ids.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));

        String finalQuery = String.format(FIND_GENRES_BY_IDS, placeholders);

        return findMany(finalQuery, ids.toArray());
    }
}
