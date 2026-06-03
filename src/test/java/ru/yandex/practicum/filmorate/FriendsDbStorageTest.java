package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.friends.FriendsDbStorage;
import ru.yandex.practicum.filmorate.dao.users.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FriendsDbStorageTest {
    private final FriendsDbStorage friendsStorage;
    private final UserDbStorage userStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDb() {
        jdbcTemplate.execute("DELETE FROM friendships");
        jdbcTemplate.execute("DELETE FROM users");
    }

    @Test
    void shouldAddFriend() {
        User u1 = userStorage.create(createUser("u1@test.com"));
        User u2 = userStorage.create(createUser("u2@test.com"));

        friendsStorage.addFriend(u1.getId(), u2.getId());

        List<User> friends = friendsStorage.getFriends(u1.getId());

        assertThat(friends)
                .extracting(User::getId)
                .contains(u2.getId());
    }

    @Test
    void shouldRemoveFriend() {
        User u1 = userStorage.create(createUser("u1@test.com"));
        User u2 = userStorage.create(createUser("u2@test.com"));

        friendsStorage.addFriend(u1.getId(), u2.getId());
        friendsStorage.removeFriend(u1.getId(), u2.getId());

        List<User> friends = friendsStorage.getFriends(u1.getId());

        assertThat(friends).isEmpty();
    }

    @Test
    void shouldReturnFriendsList() {
        User u1 = userStorage.create(createUser("u1@test.com"));
        User u2 = userStorage.create(createUser("u2@test.com"));
        User u3 = userStorage.create(createUser("u3@test.com"));

        friendsStorage.addFriend(u1.getId(), u2.getId());
        friendsStorage.addFriend(u1.getId(), u3.getId());

        List<User> friends = friendsStorage.getFriends(u1.getId());

        assertThat(friends)
                .hasSize(2)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(u2.getId(), u3.getId());
    }

    @Test
    void shouldReturnCommonFriends() {
        User u1 = userStorage.create(createUser("u1@test.com"));
        User u2 = userStorage.create(createUser("u2@test.com"));
        User u3 = userStorage.create(createUser("u3@test.com"));

        // оба добавили одного и того же пользователя
        friendsStorage.addFriend(u1.getId(), u3.getId());
        friendsStorage.addFriend(u2.getId(), u3.getId());

        List<User> common = friendsStorage.getCommonFriends(u1.getId(), u2.getId());

        assertThat(common)
                .hasSize(1)
                .extracting(User::getId)
                .containsExactly(u3.getId());
    }

    private User createUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(email);
        user.setName("Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }
}