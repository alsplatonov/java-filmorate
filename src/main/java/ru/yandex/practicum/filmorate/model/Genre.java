package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Genre {
    @NotNull(message = "ID жанра обязателен")
    private Long id;
    private String name;
}
