package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotNull(message = "Оценка полезности обязательна")
    @Positive(message = "Оценка должна быть положительной")
    Long useful;
}
