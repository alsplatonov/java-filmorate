package ru.yandex.practicum.filmorate.dao.likes;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private static final String GET_LIKES_COUNT_FOR_FILMS =
            "SELECT film_id, COUNT(user_id) as cnt FROM likes " +
                    "WHERE film_id IN (%s) " +
                    "GROUP BY film_id";

    private static final String HAS_LIKES = "SELECT COUNT(*) > 0 FROM likes WHERE user_id = ?";

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

    @Override
    public boolean hasLikes(Long userId) {
        Boolean has = jdbc.queryForObject(HAS_LIKES, Boolean.class, userId);
        return has != null && has;
    }

    public Map<Long, Integer> getLikesCountForFilms(List<Long> filmIds) {
        if (filmIds == null || filmIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String placeholders = filmIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));

        String finalQuery = String.format(GET_LIKES_COUNT_FOR_FILMS, placeholders);

        return jdbc.query(finalQuery, rs -> {
            Map<Long, Integer> map = new HashMap<>();
            while (rs.next()) {
                map.put(rs.getLong("film_id"), rs.getInt("cnt"));
            }
            return map;
        }, filmIds.toArray());
    }
}