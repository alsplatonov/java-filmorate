package ru.yandex.practicum.filmorate.dao.friends;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendsDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<User> userMapper;

    private static final String ADD_FRIEND =
            "INSERT INTO friendships(user_id, friend_id) VALUES (?, ?)";

    private static final String DELETE_FRIEND =
            "DELETE FROM friendships " +
                    "WHERE user_id = ? AND friend_id = ? ";

    private static final String GET_FRIENDS =
            "SELECT u.* " +
                    "FROM users u " +
                    "JOIN friendships f ON u.id = f.friend_id " +
                    "WHERE f.user_id = ? " +
                    "ORDER BY u.id";

    private static final String GET_COMMON_FRIENDS =
            "SELECT u.* " +
                    "FROM users u " +
                    "JOIN friendships f1 ON u.id = f1.friend_id " +
                    "JOIN friendships f2 ON u.id = f2.friend_id " +
                    "WHERE f1.user_id = ? " +
                    "AND f2.user_id = ? ";

    @Override
    public void addFriend(long userId, long friendId) {
        jdbc.update(ADD_FRIEND, userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        jdbc.update(DELETE_FRIEND,
                userId, friendId);
    }

    @Override
    public List<User> getFriends(long userId) {
        return jdbc.query(GET_FRIENDS, userMapper, userId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        return jdbc.query(GET_COMMON_FRIENDS, userMapper, userId, otherId);
    }
}