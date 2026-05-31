package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.model.Review;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewMapper {
    public static Review mapToReview(NewReviewRequest requestReview) {
        Review review = new Review();
        review.setContent(requestReview.getContent());
        review.setUseful(0L);
        review.setFilmId(requestReview.getFilmId());
        review.setUserId(requestReview.getUserId());
        review.setIsPositive(requestReview.getIsPositive());
        return review;
    }

    public static ReviewDto mapToReviewDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setReviewId(review.getReviewId());
        reviewDto.setContent(review.getContent());
        reviewDto.setUseful(review.getUseful());
        reviewDto.setFilmId(review.getFilmId());
        reviewDto.setUserId(review.getUserId());
        reviewDto.setIsPositive(review.getIsPositive());
        return reviewDto;
    }

    public static Review updateReviewFields(Review review, UpdateReviewRequest requestReview) {
        review.setReviewId(requestReview.getReviewId());
        review.setContent(requestReview.getContent());
        review.setIsPositive(requestReview.getIsPositive());
        review.setFilmId(requestReview.getFilmId());
        review.setUserId(requestReview.getUserId());
        return review;
    }
}
