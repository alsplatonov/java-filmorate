package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.event.EventStorage;
import ru.yandex.practicum.filmorate.dao.friends.FriendsDbStorage;
import ru.yandex.practicum.filmorate.dao.users.UserDbStorage;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserDbStorage userDbStorage;
    @Autowired
    private final FriendsDbStorage friendsDbStorage;
    private final EventStorage eventStorage;

    public UserDto create(NewUserRequest request) {
        User user = UserMapper.mapToUser(request);
        user = userDbStorage.create(user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto update(UpdateUserRequest request) {
        // пробуем найти пользователя, если нет — выбросится NotFoundException
        User updatedUser = userDbStorage.findById(request.getId())
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        updatedUser = userDbStorage.update(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    public Collection<UserDto> findAll() {
        return userDbStorage.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto findById(Long id) {
        if (id == null) {
            throw new ValidationException("id пользователя не может быть null");
        }
        return userDbStorage.findById(id)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + id));
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            throw new ValidationException("id пользователя или друга не может быть null");
        }
        userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        userDbStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Друг не найден"));

        friendsDbStorage.addFriend(userId, friendId);

        Event event = new Event();
        event.setTimestamp(System.currentTimeMillis());
        event.setUserId(userId);
        event.setEventType(EventType.FRIEND);
        event.setOperation(Operation.ADD);
        event.setEntityId(friendId);

        eventStorage.create(event);
    }

    public void removeFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            throw new ValidationException("id пользователя или друга не может быть null");
        }
        userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        userDbStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Друг не найден"));

        friendsDbStorage.removeFriend(userId, friendId);

        Event event = new Event();
        event.setTimestamp(System.currentTimeMillis());
        event.setUserId(userId);
        event.setEventType(EventType.FRIEND);
        event.setOperation(Operation.REMOVE);
        event.setEntityId(friendId);

        eventStorage.create(event);
    }

    //список объектов друзей, а не id
    public List<UserDto> getUserFriends(Long userId) {
        if (userId == null) {
            throw new ValidationException("id пользователя не может быть null");
        }
        userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return friendsDbStorage.getFriends(userId).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    //список общих друзей двух юзеров
    public Set<UserDto> getCommonFriends(Long firstUserId, Long secondUserId) {
        if (firstUserId == null || secondUserId == null) {
            throw new ValidationException("id полльзователей не может быть null");
        }
        userDbStorage.findById(firstUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        userDbStorage.findById(secondUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return friendsDbStorage.getCommonFriends(firstUserId, secondUserId)
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toSet());
    }

    public UserDto delete(Long userId) {
        if (userId == null) {
            throw new ValidationException("Id пользователя не должен быть null");
        }
        userDbStorage.findById(userId)
                .orElseThrow(() -> {
                    throw new NotFoundException(String.format("Пользователь с id %d не найден для удаления.\n", userId));
                });
        return UserMapper.mapToUserDto(userDbStorage.delete(userId));
    }

    public List<Event> getFeed(Long userId) {
        userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        return eventStorage.getAll(userId);
    }

}
