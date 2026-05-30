package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaRatingDbStorage extends BaseRepository<MpaRating> implements MpaRatingStorage {
    private static final String FIND_ALL_MPA = "SELECT * FROM mpa_ratings order by id";
    private static final String FIND_BY_MPA_ID = "SELECT * FROM mpa_ratings WHERE id = ?";
    private static final String FIND_BY_MAP_NAME = "SELECT * FROM mpa_ratings WHERE name = ?";

    public MpaRatingDbStorage(JdbcTemplate jdbc, RowMapper<MpaRating> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<MpaRating> findAll() {
        return findMany(FIND_ALL_MPA);
    }

    @Override
    public Optional<MpaRating> findById(long id) {
        return findOne(FIND_BY_MPA_ID, id);
    }
}
