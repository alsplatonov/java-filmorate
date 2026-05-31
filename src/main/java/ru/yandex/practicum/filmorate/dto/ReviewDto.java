package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

@Data
public class ReviewDto {
    Long reviewId;
    String content;
    Boolean isPositive;
    Long userId;
    Long filmId;
    Long useful;
}
