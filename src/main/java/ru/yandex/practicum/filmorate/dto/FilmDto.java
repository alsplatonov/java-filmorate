package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Data
public class FilmDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private MpaRatingDto mpa;
    private Set<GenreDto> genres;
    @JsonProperty("directors")
    private Set<DirectorDto> directors;
}