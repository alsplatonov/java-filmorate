package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository("dbFilmStorage")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM films ORDER BY id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa_id)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
            "duration = ? WHERE id = ?";
    private static final String INSERT_FILM_GENRE = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String FIND_POPULAR_FILM = "SELECT f.* FROM films f LEFT JOIN likes l ON f.id = l.film_id";
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
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM films WHERE id = ?";
    private static final String INSERT_FILM_DIRECTOR = "INSERT INTO film_directors (film_id, director_id)" +
            " VALUES (?, ?)";
    private static final String GET_DIRECTORS_FILMS_SORTED_BY_LIKES =
            "SELECT f.*, COUNT(l.user_id) AS likes_count " +
                    "FROM films f " +
                    "JOIN film_directors fd ON f.id = fd.film_id " +
                    "LEFT JOIN likes l ON f.id = l.film_id " +
                    "WHERE fd.director_id = ? " +
                    "GROUP BY f.id " +
                    "ORDER BY likes_count DESC";
    private static final String GET_DIRECTORS_FILMS_SORTED_BY_YEAR =
            "SELECT f.* " +
                    "FROM films f " +
                    "JOIN film_directors fd ON f.id = fd.film_id " +
                    "WHERE fd.director_id = ? " +
                    "ORDER BY f.release_date";

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
    public Film delete(Long filmId) {
        Optional<Film> deleteFilm = findById(filmId);
        if (deleteFilm.isPresent()) {
            if (delete(DELETE_BY_ID_QUERY, filmId)) {
                return deleteFilm.get();
            }
        }
        return null;
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
        saveFilmDirectors(id, film.getDirector());
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
        saveFilmGenres(film.getId(), film.getGenres());
        saveFilmDirectors(film.getId(), film.getDirector());
        return film;
    }

    @Override
    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        if ("likes".equalsIgnoreCase(sortBy)) {
            return findMany(GET_DIRECTORS_FILMS_SORTED_BY_LIKES, directorId);
        } else {
            return findMany(GET_DIRECTORS_FILMS_SORTED_BY_YEAR, directorId);
        }
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

    public List<Film> getPopular(int limit, Long genreId, Long year) {
        StringBuilder sql = new StringBuilder(FIND_POPULAR_FILM);

        //  Списки для хранения условий и параметров
        List<String> conditions = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();

        if (year != null) {
            conditions.add("EXTRACT(YEAR FROM f.release_date) = ?");
            parameters.add(year);
        }

        if (genreId != null) {
            conditions.add("f.id IN (SELECT film_id FROM film_genres WHERE genre_id = ?)");
            parameters.add(genreId);
        }

        //  Если есть хотя бы один фильтр, склеиваем их через AND и добавляем WHERE
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(String.join(" AND ", conditions));
        }

        sql.append(" GROUP BY f.id ORDER BY COUNT(l.user_id) DESC LIMIT ?");
        parameters.add(limit);

        return findMany(sql.toString(), parameters.toArray());
    }

    private void saveFilmDirectors(long filmId, Set<Director> directors) {
        if (directors == null || directors.isEmpty()) {
            return;
        }

        jdbc.batchUpdate(
                INSERT_FILM_DIRECTOR,
                directors,
                directors.size(),
                (ps, director) -> {
                    ps.setLong(1, filmId);
                    ps.setLong(2, director.getId());
                }
        );
    }
}