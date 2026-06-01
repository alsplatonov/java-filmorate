package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.List;

@Repository
public class EventDbStorage extends BaseRepository<Event> implements EventStorage {

    private static final String INSERT_QUERY =
            "INSERT INTO events(user_id, event_type, operation, entity_id, timestamp) VALUES (?, ?, ?, ?, ?)";

    private static final String FIND_BY_USER_ID_QUERY =
            "SELECT * FROM events WHERE user_id = ? ORDER BY event_id ASC";

    public EventDbStorage(JdbcTemplate jdbc) {
        super(jdbc, (rs, rowNum) -> {
            Event event = new Event();
            event.setEventId(rs.getLong("event_id"));
            event.setUserId(rs.getLong("user_id"));
            event.setEventType(EventType.valueOf(rs.getString("event_type")));
            event.setOperation(Operation.valueOf(rs.getString("operation")));
            event.setEntityId(rs.getLong("entity_id"));
            event.setTimestamp(rs.getLong("timestamp"));
            return event;
        });
    }

    @Override
    public Event create(Event event) {
        long id = insert(
                INSERT_QUERY,
                event.getUserId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId(),
                event.getTimestamp()
        );
        event.setEventId(id);
        return event;
    }

    @Override
    public List<Event> getAll(Long userId) {
        return findMany(FIND_BY_USER_ID_QUERY, userId);
    }
}