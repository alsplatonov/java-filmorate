package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDb() {
        jdbcTemplate.execute("DELETE FROM likes");
        jdbcTemplate.execute("DELETE FROM film_genres");
        jdbcTemplate.execute("DELETE FROM films");
    }

    @Test
    void shouldCreateFilm() {
        Film film = createFilm("Film 1");

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

    private Film createFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new MpaRating(1L, null));
        return film;
    }
}