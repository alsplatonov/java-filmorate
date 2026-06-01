package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    private Long timestamp;

    @NotNull(message = "userId не может быть пустым")
    private Long userId;

    private EventType eventType;
    private Operation operation;
    private Long eventId;
    private Long entityId;
}
