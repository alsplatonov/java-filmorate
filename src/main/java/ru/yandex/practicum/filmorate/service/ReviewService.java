package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;

import java.util.List;

public class ReviewService {
    public ReviewDto createReview(@Valid NewReviewRequest review) {
    }

    public ReviewDto updateReview(@Valid UpdateReviewRequest review) {
    }

    public void deleteReview(Long id) {
    }

    public ReviewDto getReviewById(Long id) {
    }

    public List<ReviewDto> getReviews(Long filmId, int count) {
    }

    public void addLike(Long id, Long userId) {
    }

    public void addDislike(Long id, Long userId) {
    }

    public void removeLike(Long id, Long userId) {
    }

    public void removeDislike(Long id, Long userId) {

    }
}
