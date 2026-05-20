package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Data
public class UpdateFilmRequest {
    @NotNull(message = "Id обязателен для обновления")
    @Positive(message = "Id должен быть положительным")
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;

    private Set<GenreDto> genres;
    private MpaRatingDto mpa;

    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }

    public boolean hasReleaseDate() {
        return releaseDate != null;
    }

    public boolean hasDuration() {
        return duration != null && duration > 0;
    }

    public boolean hasMpa() {
        return mpa != null;
    }

    public boolean hasGenres() {
        return genres != null && !genres.isEmpty();
    }
}