package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {
    Review create(Review review);

    Optional<Review> findById(Long reviewId);

    Review update(Review review);

    void removeReview(Long id);

    Collection<Review> findReviewsByFilmId(Long filmId, int count);
}
