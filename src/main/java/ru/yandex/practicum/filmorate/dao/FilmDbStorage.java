package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
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
    private static final String INSERT_FILM_DIRECTOR = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";
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
