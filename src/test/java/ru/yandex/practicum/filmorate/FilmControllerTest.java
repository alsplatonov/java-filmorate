package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Film validFilm;

    @BeforeEach
    void setUp() {
        validFilm = new Film();
        validFilm.setName("Test Film");
        validFilm.setDescription("Описание фильма");
        validFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        validFilm.setDuration(120);
    }

    private User createUser() throws Exception {
        String json = "{\n" +
                "  \"email\": \"test@test.com\",\n" +
                "  \"login\": \"user\",\n" +
                "  \"name\": \"User\",\n" +
                "  \"birthday\": \"1990-01-01\"\n" +
                "}";

        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, User.class);
    }

    private Film createFilm() throws Exception {
        String response = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, Film.class);
    }

    @Test
    void createFilm_EmptyRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFilm_ValidFilm_ReturnsCreatedFilm() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Film"));
    }

    @Test
    void createFilm_EmptyName_ReturnsBadRequest() throws Exception {
        validFilm.setName("");

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFilm_NegativeDuration_ReturnsBadRequest() throws Exception {
        validFilm.setDuration(-10);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllFilms_ReturnsFilmList() throws Exception {
        createFilm();

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").value("Test Film"));
    }

    @Test
    void getFilmById_ReturnsFilm() throws Exception {
        Film film = createFilm();

        mockMvc.perform(get("/films/{id}", film.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(film.getId()))
                .andExpect(jsonPath("$.name").value("Test Film"));
    }

    @Test
    void setLike_ShouldReturnNoContent() throws Exception {
        User user = createUser();
        Film film = createFilm();

        mockMvc.perform(put("/films/{id}/like/{userId}", film.getId(), user.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeLike_ShouldReturnNoContent() throws Exception {
        User user = createUser();
        Film film = createFilm();

        // ставим лайк
        mockMvc.perform(put("/films/{id}/like/{userId}", film.getId(), user.getId()))
                .andExpect(status().isNoContent());

        // удаляем лайк
        mockMvc.perform(delete("/films/{id}/like/{userId}", film.getId(), user.getId()))
                .andExpect(status().isNoContent());
    }
}