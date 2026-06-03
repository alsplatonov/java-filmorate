package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.directors.DirectorDbStorage;
import ru.yandex.practicum.filmorate.model.Director;
import java.util.List;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DirectorDbStorageTest {

    private final DirectorDbStorage directorDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDb() {
        jdbcTemplate.execute("DELETE FROM film_directors");
        jdbcTemplate.execute("DELETE FROM directors");
        jdbcTemplate.execute("DELETE FROM films");
    }

    @Test
    void shouldCreateDirector() {
        Director director = createDirector("Director 1");

        Director created = directorDbStorage.create(director);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Director 1");
    }

    @Test
    void shouldFindDirectorById() {
        Director created = directorDbStorage.create(createDirector("Tarantino"));

        Director found = directorDbStorage.findById(created.getId()).orElseThrow();

        assertThat(found.getName()).isEqualTo("Tarantino");
    }

    @Test
    void shouldUpdateDirector() {
        Director director = directorDbStorage.create(createDirector("Old Name"));

        director.setName("Updated Name");

        Director updated = directorDbStorage.update(director);

        assertThat(updated.getName()).isEqualTo("Updated Name");
    }

    @Test
    void shouldFindAllDirectors() {
        directorDbStorage.create(createDirector("Director 1"));
        directorDbStorage.create(createDirector("Director 2"));

        List<Director> directors = directorDbStorage.findAll();

        assertThat(directors).hasSize(2);
    }

    @Test
    void shouldFindDirectorsByIds() {
        Director d1 = directorDbStorage.create(createDirector("Director 1"));
        Director d2 = directorDbStorage.create(createDirector("Director 2"));
        Director d3 = directorDbStorage.create(createDirector("Director 3"));

        Set<Long> ids = Set.of(d1.getId(), d3.getId());

        List<Director> result = directorDbStorage.findDirectorsByIds(ids);

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Director::getName)
                .containsExactlyInAnyOrder("Director 1", "Director 3");
    }

    @Test
    void shouldFindDirectorByFilmId() {
        Director director = directorDbStorage.create(createDirector("Nolan"));

        jdbcTemplate.update(
                "INSERT INTO films(name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)",
                "Film",
                "desc",
                "2000-01-01",
                120,
                1
        );

        Long filmId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM films", Long.class);

        jdbcTemplate.update(
                "INSERT INTO film_directors(film_id, director_id) VALUES (?, ?)",
                filmId,
                director.getId()
        );

        Set<Director> result = directorDbStorage.findByFilmId(filmId);

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getName()).isEqualTo("Nolan");
    }

    private Director createDirector(String name) {
        Director director = new Director();
        director.setName(name);
        return director;
    }
}