package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewLikesDbStorage implements ReviewLikesStorage {

    private final JdbcTemplate jdbc;

    private static final String CREATE_REACTION = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, ?)";

    private static final String REMOVE_REACTION = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = ?";

    @Override
    public void addLike(Long reviewId, Long userId) {
        jdbc.update(CREATE_REACTION, reviewId, userId, true);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        jdbc.update(CREATE_REACTION, reviewId, userId, false);
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        jdbc.update(REMOVE_REACTION, reviewId, userId, true);
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        jdbc.update(REMOVE_REACTION, reviewId, userId, false);
    }
}
