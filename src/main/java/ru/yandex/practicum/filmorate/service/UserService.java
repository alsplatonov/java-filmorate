package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendsDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDbStorage userDbStorage;
    private final FriendsDbStorage friendsDbStorage;

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
                .sorted(Comparator.comparing(UserDto::getId))
                .collect(Collectors.toList());
    }

    public UserDto findById(Long id) {
        return userDbStorage.findById(id)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + id));
    }

    public void addFriend(Long userId, Long friendId) {
        userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        userDbStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Друг не найден"));

        friendsDbStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        userDbStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Друг не найден"));

        friendsDbStorage.removeFriend(userId, friendId);
    }

    //список объектов друзей, а не id
    public Set<UserDto> getUserFriends(Long userId) {
        userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return friendsDbStorage.getFriends(userId).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toSet());
    }

    //список общих друзей двух юзеров
    public Set<UserDto> getCommonFriends(Long firstUserId, Long secondUserId) {

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

}
