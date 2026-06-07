package ru.yandex.practicum.filmorate.dao.event;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    Event create(Event event);

    List<Event> getAll(Long userId);
}
