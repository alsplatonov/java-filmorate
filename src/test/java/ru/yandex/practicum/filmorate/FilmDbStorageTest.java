package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.directors.DirectorDbStorage;
import ru.yandex.practicum.filmorate.dao.films.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.users.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final DirectorDbStorage directorStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserDbStorage userStorage;

    @BeforeEach
    void cleanDb() {
        jdbcTemplate.execute("DELETE FROM likes");
        jdbcTemplate.execute("DELETE FROM film_genres");
        jdbcTemplate.execute("DELETE FROM film_directors");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM directors");
    }

    @Test
    void shouldCreateFilmWithDirector() {
        Director director = directorStorage.create(createDirector("Nolan"));

        Film film = createFilm("Film 1");
        film.setDirectors(Set.of(director));

        Film created = filmStorage.create(film);

        assertThat(created.getId()).isNotNull();
    }

    @Test
    void shouldFindFilmById() {
        Film created = filmStorage.create(createFilm("Film 1"));

        Film found = filmStorage.findById(created.getId()).orElseThrow();

        assertThat(found.getName()).isEqualTo("Film 1");
    }

    @Test
    void shouldUpdateFilm() {
        Film film = filmStorage.create(createFilm("Old"));

        film.setName("Updated");

        Film updated = filmStorage.update(film);

        assertThat(updated.getName()).isEqualTo("Updated");
    }

    @Test
    void shouldFindAllFilms() {
        filmStorage.create(createFilm("F1"));
        filmStorage.create(createFilm("F2"));

        List<Film> films = filmStorage.findAll();

        assertThat(films).hasSize(2);
    }

    @Test
    void shouldGetFilmsByDirectorSortedByYear() {
        Director director = directorStorage.create(createDirector("Nolan"));

        Film f1 = createFilm("F1");
        f1.setReleaseDate(LocalDate.of(2001, 1, 1));
        f1.setDirectors(Set.of(director));

        Film f2 = createFilm("F2");
        f2.setReleaseDate(LocalDate.of(1999, 1, 1));
        f2.setDirectors(Set.of(director));

        filmStorage.create(f1);
        filmStorage.create(f2);

        List<Film> films = filmStorage.getFilmsByDirector(director.getId(), "year");

        assertThat(films).hasSize(2);
        assertThat(films.get(0).getReleaseDate())
                .isBefore(films.get(1).getReleaseDate());
    }

    @Test
    void shouldGetFilmsByDirectorSortedByLikes() {
        Director director = directorStorage.create(createDirector("Nolan"));

        User u1 = userStorage.create(createUser("u1"));
        User u2 = userStorage.create(createUser("u2"));
        User u3 = userStorage.create(createUser("u3"));

        Film f1 = createFilm("F1");
        f1.setDirectors(Set.of(director));
        f1 = filmStorage.create(f1);

        Film f2 = createFilm("F2");
        f2.setDirectors(Set.of(director));
        f2 = filmStorage.create(f2);

        // имитируем лайки
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", f2.getId(), u1.getId());
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", f2.getId(), u2.getId());

        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", f1.getId(), u3.getId());

        List<Film> films = filmStorage.getFilmsByDirector(director.getId(), "likes");

        assertThat(films.get(0).getId()).isEqualTo(f2.getId());
    }

    @Test
    void shouldSearchFilmByTitle() {
        Film film = createFilm("Inception");
        filmStorage.create(film);

        List<Film> result = filmStorage.searchBy("Incep", "title");

        assertThat(result)
                .isNotEmpty();
        assertThat(result.get(0).getName())
                .containsIgnoringCase("inception");
    }

    @Test
    void shouldSearchFilmByDirector() {
        Director director = directorStorage.create(createDirector("Nolan"));

        Film film = createFilm("Interstellar");
        film.setDirectors(Set.of(director));
        filmStorage.create(film);

        List<Film> result = filmStorage.searchBy("Nol", "director");

        assertThat(result)
                .isNotEmpty();
        assertThat(result.get(0).getName())
                .isEqualTo("Interstellar");
    }

    @Test
    void shouldSearchFilmByTitleAndDirector() {
        Director director = directorStorage.create(createDirector("Nolan"));

        Film film1 = createFilm("Matrix");
        film1.setDirectors(Set.of(director));
        filmStorage.create(film1);

        Film film2 = createFilm("Avatar");
        filmStorage.create(film2);

        List<Film> result = filmStorage.searchBy("Matrix", "title,director");

        assertThat(result)
                .hasSize(1);

        assertThat(result.get(0).getName())
                .isEqualTo("Matrix");
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

    private Director createDirector(String name) {
        Director director = new Director();
        director.setName(name);
        return director;
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