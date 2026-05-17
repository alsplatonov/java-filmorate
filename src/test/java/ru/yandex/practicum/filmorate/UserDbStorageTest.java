package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.jdbc.core.JdbcTemplate;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final UserDbStorage userDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDb() {
        jdbcTemplate.execute("DELETE FROM users");
    }

    @Test
    void shouldCreateUser() {
        User user = createUser("test@test.com");

        User created = userDbStorage.create(user);

        assertThat(created.getId()).isNotNull();
    }

    @Test
    void shouldFindUserById() {
        User created = userDbStorage.create(createUser("test@test.com"));

        Optional<User> found = userDbStorage.findById(created.getId());

        assertThat(found).isPresent();
    }

    @Test
    void shouldUpdateUser() {
        User user = userDbStorage.create(createUser("test@test.com"));

        user.setName("Updated");

        User updated = userDbStorage.update(user);

        assertThat(updated.getName()).isEqualTo("Updated");
    }

    @Test
    void shouldFindAllUsers() {
        userDbStorage.create(createUser("u1@test.com"));
        userDbStorage.create(createUser("u2@test.com"));

        List<User> users = userDbStorage.findAll();

        assertThat(users).hasSize(2);
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