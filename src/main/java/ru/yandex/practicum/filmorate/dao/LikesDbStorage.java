package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LikesDbStorage implements LikesStorage {

    private final JdbcTemplate jdbc;

    private static final String CREATE_LIKE =
            "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";

    private static final String REMOVE_LIKE =
            "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

    private static final String GET_LIKES_COUNT =
            "SELECT COUNT(*) FROM likes WHERE film_id = ?";

    private static final String CHECK_LIKE_EXISTS =
            "SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?";

    @Override
    public void addLike(long filmId, long userId) {
        jdbc.update(CREATE_LIKE, filmId, userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        jdbc.update(REMOVE_LIKE, filmId, userId);
    }

    @Override
    public int getLikesCount(long filmId) {
        return jdbc.queryForObject(GET_LIKES_COUNT, Integer.class, filmId);
    }

    @Override
    public boolean isLiked(long filmId, long userId) {
        Integer count = jdbc.queryForObject(CHECK_LIKE_EXISTS, Integer.class, filmId, userId);
        return count != null && count > 0;
    }
}