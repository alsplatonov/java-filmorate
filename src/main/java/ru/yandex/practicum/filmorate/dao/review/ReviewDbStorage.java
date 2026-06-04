package ru.yandex.practicum.filmorate.dao.review;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

@Repository
public class ReviewDbStorage extends BaseRepository<Review> implements ReviewStorage {
    private static final String INSERT_QUERY = "INSERT INTO reviews(content, is_positive, user_id, film_id, useful) VALUES (?, ?, ?, ?, ?)";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM reviews WHERE id = ?";

    private static final String UPDATE_QUERY = "UPDATE reviews SET content = ?, is_positive = ?, user_id = ?, film_id = ?, useful = ? WHERE id = ?";

    private static final String DELETE_QUERY = "DELETE FROM reviews WHERE id = ?";

    private static final String FIND_REVIEWS_BY_FILM_ID = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";

    private static final String FIND_REVIEWS_WITHOUT_FILM_ID = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";

    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Review create(Review review) {
        long id = insert(
                INSERT_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful()
        );
        review.setReviewId(id);
        return review;
    }

    @Override
    public Optional<Review> findById(Long reviewId) {
        return findOne(FIND_BY_ID_QUERY, reviewId);
    }

    @Override
    public Review update(Review review) {
        update(
                UPDATE_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful(),
                review.getReviewId()
        );
        return review;
    }

    @Override
    public void removeReview(Long id) {
        delete(DELETE_QUERY, id);
    }

    @Override
    public Collection<Review> findReviewsByFilmId(Long filmId, int count) {
        if (filmId == null) {
            return findMany(FIND_REVIEWS_WITHOUT_FILM_ID, count);
        }
        return findMany(
                FIND_REVIEWS_BY_FILM_ID,
                filmId,
                count);
    }
}
