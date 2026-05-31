package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewReviewRequest {
    @NotBlank(message = "Описание отзыва не может быть пустым")
    String content;

    @NotNull(message = "Оценка положительности обязательна")
    Boolean isPositive;

    @NotNull(message = "Id пользователя обязательно")
    Long userId;

    @NotNull(message = "Id фильма обязательно")
    Long filmId;
}
