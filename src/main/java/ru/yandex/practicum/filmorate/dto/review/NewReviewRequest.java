package ru.yandex.practicum.filmorate.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewReviewRequest {
    @NotBlank(message = "Описание отзыва не может быть пустым")
    private String content;

    @NotNull(message = "Оценка положительности обязательна")
    private Boolean isPositive;

    @NotNull(message = "Id пользователя обязательно")
    private Long userId;

    @NotNull(message = "Id фильма обязательно")
    private Long filmId;
}
