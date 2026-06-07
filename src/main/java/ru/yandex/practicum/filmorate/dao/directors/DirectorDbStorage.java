package ru.yandex.practicum.filmorate.dao.directors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class DirectorDbStorage extends BaseRepository<Director> implements DirectorStorage {
    private static final String FIND_ALL_DIRECTORS = "SELECT * FROM directors ORDER BY id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM directors WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO directors(name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE id = ?";
    private static final String FIND_DIRECTORS_BY_IDS = "SELECT * FROM directors WHERE id IN (%s)";
    private static final String DELETE_QUERY = "DELETE FROM directors WHERE id = ?";
    private static final String FIND_DIRECTOR_BY_FILM_ID =
            "SELECT g.id, g.name " +
                    "FROM film_directors fd " +
                    "JOIN directors g ON fd.director_id = g.id " +
                    "WHERE fd.film_id = ? " +
                    "ORDER BY g.id";

    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Director> findAll() {
        return findMany(FIND_ALL_DIRECTORS);
    }

    @Override
    public Optional<Director> findById(Long directorId) {
        return findOne(FIND_BY_ID_QUERY, directorId);
    }

    @Override
    public Director create(Director director) {
        long id = insert(
                INSERT_QUERY,
                director.getName()
        );
        director.setId(id);
        return director;
    }

    @Override
    public Director update(Director director) {
        update(
                UPDATE_QUERY,
                director.getName(),
                director.getId()
        );
        return director;
    }

    @Override
    public void removeDirector(Long directorId) {
        delete(DELETE_QUERY, directorId);
    }

    public Set<Director> findByFilmId(long filmId) {
        return new LinkedHashSet<>(
                findMany(FIND_DIRECTOR_BY_FILM_ID, filmId)
        );
    }

    public List<Director> findDirectorsByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        String placeholders = ids.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));

        String finalQuery = String.format(FIND_DIRECTORS_BY_IDS, placeholders);

        return findMany(finalQuery, ids.toArray());
    }
}



