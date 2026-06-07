package ru.yandex.practicum.filmorate.dao.friends;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsStorage {
    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    List<User> getFriends(long userId);

    List<User> getCommonFriends(long userId, long otherId);
}