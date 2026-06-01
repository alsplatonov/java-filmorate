package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventStorage;
import ru.yandex.practicum.filmorate.dao.ReviewDbStorage;
import ru.yandex.practicum.filmorate.dao.ReviewLikesDbStorage;
import ru.yandex.practicum.filmorate.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDbStorage reviewDbStorage;
    private final ReviewLikesDbStorage reviewLikesDbStorage;
    private final EventStorage eventStorage;

    public ReviewDto create(NewReviewRequest requestReview) {
        if (requestReview.getFilmId() == null || requestReview.getFilmId() <= 0) {
            throw new NotFoundException("Id фильма должно быть положительным числом");
        }
        if (requestReview.getUserId() == null || requestReview.getUserId() <= 0) {
            throw new NotFoundException("Id пользователя должно быть положительным числом");
        }
        Review review = ReviewMapper.mapToReview(requestReview);
        review = reviewDbStorage.create(review);

        Event event = new Event();
        event.setTimestamp(System.currentTimeMillis());
        event.setUserId(review.getUserId());
        event.setEventType(EventType.REVIEW);
        event.setOperation(Operation.ADD);
        event.setEntityId(review.getReviewId());
        eventStorage.create(event);

        return ReviewMapper.mapToReviewDto(review);
    }

    public ReviewDto update(UpdateReviewRequest requestReview) {
        if (requestReview.getFilmId() == null || requestReview.getFilmId() <= 0) {
            throw new NotFoundException("Id фильма должно быть положительным числом");
        }
        if (requestReview.getUserId() == null || requestReview.getUserId() <= 0) {
            throw new NotFoundException("Id пользователя должно быть положительным числом");
        }
        Review updateReview = reviewDbStorage.findById(requestReview.getReviewId())
                .map(review -> ReviewMapper.updateReviewFields(review, requestReview))
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        updateReview = reviewDbStorage.update(updateReview);

        Event event = new Event();
        event.setTimestamp(System.currentTimeMillis());
        event.setUserId(updateReview.getUserId());
        event.setEventType(EventType.REVIEW);
        event.setOperation(Operation.UPDATE);
        event.setEntityId(updateReview.getReviewId());
        eventStorage.create(event);

        return ReviewMapper.mapToReviewDto(updateReview);
    }

    public void delete(Long id) {
        Review review = reviewDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден с ID: " + id));

        reviewDbStorage.removeReview(id);

        Event event = new Event();
        event.setTimestamp(System.currentTimeMillis());
        event.setUserId(review.getUserId());
        event.setEventType(EventType.REVIEW);
        event.setOperation(Operation.REMOVE);
        event.setEntityId(id);
        eventStorage.create(event);

    }

    public ReviewDto findById(Long id) {
        return reviewDbStorage.findById(id)
                .map(ReviewMapper::mapToReviewDto)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден с ID: " + id));
    }

    public List<ReviewDto> findReviewsByFilmId(Long filmId, int count) {
        return reviewDbStorage.findReviewsByFilmId(filmId, count).stream()
                .map(ReviewMapper::mapToReviewDto)
                .collect(Collectors.toList());
    }

    public void addLike(Long id, Long userId) {
        reviewLikesDbStorage.addLike(id, userId);
    }

    public void addDislike(Long id, Long userId) {
        reviewLikesDbStorage.addDislike(id, userId);
    }

    public void removeLike(Long id, Long userId) {
        reviewLikesDbStorage.removeLike(id, userId);
    }

    public void removeDislike(Long id, Long userId) {
        reviewLikesDbStorage.removeDislike(id, userId);
    }
}
