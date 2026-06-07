package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmExtendedRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));

        Long mpaId = resultSet.getObject("mpa_id", Long.class);
        String mpaName = resultSet.getString("mpa_name");

        Long genreId = resultSet.getObject("genre_id", Long.class);
        String genreName = resultSet.getString("genre_name");

        Long directorId = resultSet.getObject("director_id", Long.class);
        String directorName = resultSet.getString("director_name");

        if (mpaId != null) {
            MpaRating mpa = new MpaRating();
            mpa.setId(mpaId);
            mpa.setName(mpaName);
            film.setMpa(mpa);
        }

        if (genreId != null) {
            Genre genre = new Genre();
            genre.setId(genreId);
            genre.setName(genreName);
            film.addGenres(genre);
        }

        if (directorId != null) {
            Director director = new Director();
            director.setId(directorId);
            director.setName(directorName);
            film.addDirector(director);
        }

        return film;
    }
}
