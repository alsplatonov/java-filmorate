package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User create(User user) {
        normalizeUserName(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        // пробуем найти пользователя, если нет — выбросится NotFoundException
        User existingUser = userStorage.findById(user.getId());
        // нормализуем имя и обновляем
        normalizeUserName(user);
        return userStorage.update(user);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(Long id) {
        return userStorage.findById(id);
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }
    //список объектов друзей, а не id
    public Set<User> getUserFriends(Long userId) {
        User user = findById(userId);
        return user.getFriends().stream()
                .map(id -> findById(id))
                .collect(Collectors.toSet());
    }
    //список общих друзей двух юзеров
    public Set<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        User user1 = userStorage.findById(firstUserId);
        User user2 = userStorage.findById(secondUserId);
        Set<Long> commonFriendsIds = new HashSet<>(user1.getFriends());
        commonFriendsIds.retainAll(user2.getFriends());
        return commonFriendsIds.stream()
                .map(id -> findById(id))
                .collect(Collectors.toSet());
    }

    private void normalizeUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
