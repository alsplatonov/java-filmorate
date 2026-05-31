package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class Review {
    private Long reviewId;

    @NotBlank(message = "Описание отзыва не может быть пустым")
    private String content;

    @NotNull(message = "Оценка положительности обязательна")
    private Boolean isPositive;

    @NotNull(message = "Id пользователя обязательно")
    private Long userId;

    @NotNull(message = "Id фильма обязательно")
    private Long filmId;

    @NotNull(message = "Оценка полезности обязательна")
    @Positive(message = "Оценка должна быть положительной")
    private Long useful;
}
