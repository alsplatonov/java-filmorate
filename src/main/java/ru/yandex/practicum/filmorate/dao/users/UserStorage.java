package ru.yandex.practicum.filmorate.dao.users;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User create(User user);

    User update(User user);

    Collection<User> findAll();

    Optional<User> findById(Long id);

    Collection<Long> findSimilarUser(Long userId);

    User delete(Long userId);

}

