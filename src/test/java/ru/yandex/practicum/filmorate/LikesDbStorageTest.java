package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.LikesDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class LikesDbStorageTest {
    private final LikesDbStorage likesStorage;
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDb() {
        jdbcTemplate.execute("DELETE FROM likes");
        jdbcTemplate.execute("DELETE FROM film_genres");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM friendships");
        jdbcTemplate.execute("DELETE FROM users");
    }

    @Test
    void shouldAddLike() {
        Film film = filmDbStorage.create(createFilm("Film"));
        User user = userDbStorage.create(createUser("test@test.com"));

        likesStorage.addLike(film.getId(), user.getId());

        assertThat(likesStorage.isLiked(film.getId(), user.getId())).isTrue();
    }

    @Test
    void shouldRemoveLike() {
        Film film = filmDbStorage.create(createFilm("Film"));
        User user = userDbStorage.create(createUser("test@test.com"));

        likesStorage.addLike(film.getId(), user.getId());
        likesStorage.removeLike(film.getId(), user.getId());

        assertThat(likesStorage.isLiked(film.getId(), user.getId())).isFalse();
    }

    @Test
    void shouldCountLikes() {
        Film film = filmDbStorage.create(createFilm("Film"));

        User u1 = userDbStorage.create(createUser("1@test.com"));
        User u2 = userDbStorage.create(createUser("2@test.com"));
        User u3 = userDbStorage.create(createUser("3@test.com"));

        likesStorage.addLike(film.getId(), u1.getId());
        likesStorage.addLike(film.getId(), u2.getId());
        likesStorage.addLike(film.getId(), u3.getId());

        assertThat(likesStorage.getLikesCount(film.getId())).isEqualTo(3);
    }

    @Test
    void shouldReturnZeroLikesForFilm() {
        assertThat(likesStorage.getLikesCount(999L)).isEqualTo(0);
    }

    @Test
    void shouldCheckLikeExists() {
        Film film = filmDbStorage.create(createFilm("Film"));
        User user = userDbStorage.create(createUser("test@test.com"));

        likesStorage.addLike(film.getId(), user.getId());

        assertThat(likesStorage.isLiked(film.getId(), user.getId())).isTrue();
        assertThat(likesStorage.isLiked(film.getId(), 999L)).isFalse();
    }

    private Film createFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new MpaRating(1L, null));
        return film;
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