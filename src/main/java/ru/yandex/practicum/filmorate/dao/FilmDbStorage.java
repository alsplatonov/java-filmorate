package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Repository("dbFilmStorage")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM films ORDER BY id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa_id)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
            "duration = ? WHERE id = ?";
    private static final String INSERT_FILM_GENRE = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String FIND_RECOMMENDATIONS = """
            SELECT *
            FROM films f
            LEFT JOIN mpa_ratings mr ON f.mpa_id = mr.id
            WHERE f.id IN
                (
                SELECT l.film_id
                FROM likes l
                WHERE l.user_id = ? AND l.film_id
                NOT IN
                    (
                    SELECT l2.film_id
                    FROM likes l2
                    WHERE l2.user_id = ?
                    )
                )
            ORDER BY f.id
            """;
    @Autowired
    LikesStorage likesStorage;
    @Autowired
    UserStorage userStorage;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Film> findById(Long userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    @Override
    public Collection<Film> findRecommendations(Long userId) {
        if (!likesStorage.hasLikes(userId)) {
            return Collections.emptyList();
        }
        List<Long> similarUsers = userStorage.findSimilarUser(userId).stream().toList();
        if (similarUsers.isEmpty()) {
            return Collections.emptyList();
        }
        Long similarUserId = similarUsers.getFirst();
        return findMany(FIND_RECOMMENDATIONS, similarUserId, userId);
    }

    @Override
    public Film create(Film film) {
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        saveFilmGenres(id, film.getGenres());
        return film;
    }

    @Override
    public Film update(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId()
        );
        return film;
    }

    private void saveFilmGenres(long filmId, Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }

        jdbc.batchUpdate(
                INSERT_FILM_GENRE,
                genres,
                genres.size(),
                (ps, genre) -> {
                    ps.setLong(1, filmId);
                    ps.setLong(2, genre.getId());
                }
        );
    }
}
