package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    public final UserStorage userStorage;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        normalizeUserName(user);
        User newUser = userStorage.create(user);
        log.info("Создан пользователь: {}", newUser);
        return newUser;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        normalizeUserName(user);
        User updatedUser = userStorage.update(user);
        log.info("Обновлён пользователь: {}", updatedUser);
        return updatedUser;
    }

    @GetMapping
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    private void normalizeUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
