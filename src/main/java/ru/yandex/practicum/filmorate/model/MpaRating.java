package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MpaRating {
    @NotNull(message = "ID MPA обязателен")
    private Long id;
    private String name;
}