package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateDirectorRequest {
    @NotNull(message = "id не может быть пустым")
    private Long id;

    @NotBlank(message = "Имя режиссера не может быть пустым")
    private String name;
}
