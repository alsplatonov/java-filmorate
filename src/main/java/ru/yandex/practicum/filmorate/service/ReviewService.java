package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.ReviewDbStorage;
import ru.yandex.practicum.filmorate.dao.ReviewLikesDbStorage;
import ru.yandex.practicum.filmorate.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDbStorage reviewDbStorage;
    private final ReviewLikesDbStorage reviewLikesDbStorage;

    public ReviewDto create(NewReviewRequest requestReview) {
        Review review = ReviewMapper.mapToReview(requestReview);
        review = reviewDbStorage.create(review);
        return ReviewMapper.mapToReviewDto(review);
    }

    public ReviewDto update(UpdateReviewRequest requestReview) {
        Review updateReview = reviewDbStorage.findById(requestReview.getReviewId())
                .map(review -> ReviewMapper.updateReviewFields(review, requestReview))
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        updateReview = reviewDbStorage.update(updateReview);
        return ReviewMapper.mapToReviewDto(updateReview);
    }

    public void delete(Long id) {
        reviewDbStorage.removeReview(id);
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
