package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto createReview(@Valid @RequestBody NewReviewRequest review) {
        ReviewDto newReview = reviewService.create(review);
        log.info("Добавлен отзыв: {}", newReview);
        return newReview;
    }

    @PutMapping
    public ReviewDto updateReview(@Valid @RequestBody UpdateReviewRequest review) {
        ReviewDto updatedReview = reviewService.update(review);
        log.info("Обновлен отзыв: {}", updatedReview);
        return updatedReview;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable Long id) {
        reviewService.delete(id);
        log.info("Удален отзыв с id: {}", id);
    }

    @GetMapping("/{id}")
    public ReviewDto getReviewById(@PathVariable Long id) {
        ReviewDto review = reviewService.findById(id);
        log.info("Получен отзыв с id: {}", id);
        return review;
    }

    @GetMapping
    public List<ReviewDto> getReviews(
            @RequestParam(required = false) Long filmId,
            @RequestParam(required = false, defaultValue = "10") int count) {
        List<ReviewDto> reviews = reviewService.findReviewsByFilmId(filmId, count);
        log.info("Получены отзывы для filmId: {}, count: {}", filmId, count);
        return reviews;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addLike(id, userId);
        log.info("Пользователь {} поставил лайк отзыву {}", userId, id);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addDislike(id, userId);
        log.info("Пользователь {} поставил дизлайк отзыву {}", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeLike(id, userId);
        log.info("Пользователь {} удалил лайк у отзыва {}", userId, id);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeDislike(id, userId);
        log.info("Пользователь {} удалил дизлайк у отзыва {}", userId, id);
    }
}
