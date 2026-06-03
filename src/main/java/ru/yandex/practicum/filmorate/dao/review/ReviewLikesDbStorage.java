package ru.yandex.practicum.filmorate.dao.review;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewLikesDbStorage implements ReviewLikesStorage {

    private final JdbcTemplate jdbc;

    private static final String CREATE_REACTION = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, ?)";

    private static final String REMOVE_REACTION = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = ?";

    private static final String UPDATE_USEFUL = "UPDATE reviews " +
            "SET useful = (" +
            "SELECT COUNT(*) " +
            "FROM review_likes " +
            "WHERE review_id = ? AND is_like = true" +
            ") - (" +
            "SELECT COUNT(*) " +
            "FROM review_likes " +
            "WHERE review_id = ? AND is_like = false) " +
            "WHERE id = ?";

    @Override
    public void addLike(Long reviewId, Long userId) {
        jdbc.update(REMOVE_REACTION, reviewId, userId, false);
        jdbc.update(CREATE_REACTION, reviewId, userId, true);
        updateUseful(reviewId);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        jdbc.update(REMOVE_REACTION, reviewId, userId, true);
        jdbc.update(CREATE_REACTION, reviewId, userId, false);
        updateUseful(reviewId);
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        jdbc.update(REMOVE_REACTION, reviewId, userId, true);
        updateUseful(reviewId);
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        jdbc.update(REMOVE_REACTION, reviewId, userId, false);
        updateUseful(reviewId);
    }

    private void updateUseful(Long reviewId) {
        jdbc.update(UPDATE_USEFUL, reviewId, reviewId, reviewId);
    }
}
