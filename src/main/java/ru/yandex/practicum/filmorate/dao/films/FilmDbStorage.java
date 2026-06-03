package ru.yandex.practicum.filmorate.dao.films;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.dao.likes.LikesStorage;
import ru.yandex.practicum.filmorate.dao.users.UserStorage;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Slf4j
@Repository("dbFilmStorage")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM films ORDER BY id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa_id)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
            "duration = ?, mpa_id = ? WHERE id = ?";
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
    public Optional<Film> findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
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
                film.getMpa() != null ? film.getMpa().getId() : null
        );
        film.setId(id);
        saveFilmGenres(id, film.getGenres());
        saveFilmDirectors(id, film.getDirectors());
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
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId()
        );
        saveFilmGenres(film.getId(), film.getGenres());
        saveFilmDirectors(film.getId(), film.getDirectors());
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

    @Override
    public List<Film> searchBy(String query, String by) {
        String sql = createSQLQuery(by);

        String pattern = "%" + query.toLowerCase() + "%";

        List<Object> params = new ArrayList<>();

        if (by.contains("title")) {
            params.add(pattern);
        }
        if (by.contains("director")) {
            params.add(pattern);
        }

        return findMany(sql, params.toArray());
    }

    private void saveFilmGenres(long filmId, Set<Genre> genres) {
        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
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
        StringBuilder sql = new StringBuilder(
                "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                        "mr.id as mpa_id, mr.name as mpa_name, " +
                        "COUNT(l.user_id) as likes_count " +
                        "FROM films f " +
                        "LEFT JOIN likes l ON f.id = l.film_id " +
                        "LEFT JOIN mpa_ratings mr ON f.mpa_id = mr.id " +
                        "LEFT JOIN film_genres fg ON f.id = fg.film_id "
        );

        List<String> conditions = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();

        if (year != null) {
            conditions.add("EXTRACT(YEAR FROM f.release_date) = ?");
            parameters.add(year);
        }

        if (genreId != null) {
            conditions.add("fg.genre_id = ?");
            parameters.add(genreId);
        }

        // Собираем WHERE
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        sql.append(" GROUP BY f.id, f.name, f.description, f.release_date, f.duration, mr.id, mr.name");
        sql.append(" ORDER BY likes_count DESC, f.id ASC");
        sql.append(" LIMIT ?");
        parameters.add(limit);

        return findMany(sql.toString(), parameters.toArray());
    }

    private void saveFilmDirectors(long filmId, Set<Director> directors) {
        jdbc.update("DELETE FROM film_directors WHERE film_id = ?", filmId);
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

    private String createSQLQuery(String by) {
        Set<String> filters = Set.of(by.split(","));

        boolean byTitle = filters.contains("title");
        boolean byDirector = filters.contains("director");

        String where;
        if (byTitle && byDirector) {
            where = "(LOWER(f.name) LIKE ? OR LOWER(d.name) LIKE ?)";
        } else if (byTitle) {
            where = "LOWER(f.name) LIKE ?";
        } else {
            where = "LOWER(d.name) LIKE ?";
        }
        return "SELECT f.*, COUNT(fl.user_id) as likes_count " +
                "FROM films f LEFT JOIN likes fl ON f.id = fl.film_id " +
                "LEFT JOIN film_directors fd ON f.id = fd.film_id " +
                "LEFT JOIN directors d ON fd.director_id = d.id " +
                "WHERE " + where + " GROUP BY f.id ORDER BY likes_count DESC ";
    }
}