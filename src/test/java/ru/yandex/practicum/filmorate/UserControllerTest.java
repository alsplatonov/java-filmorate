package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User();
        validUser.setEmail("test@test.com");
        validUser.setLogin("newLogin");
        validUser.setName("Test User");
        validUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void createUser_EmptyRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_ValidUser_ReturnsCreatedUser() throws Exception {
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User createdUser = objectMapper.readValue(response, User.class);
    }

    @Test
    void createUser_InvalidEmail_ReturnsBadRequest() throws Exception {
        validUser.setEmail("invalid-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_EmptyLogin_ReturnsBadRequest() throws Exception {
        validUser.setLogin("");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_ReturnsUser() throws Exception {
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andReturn().getResponse().getContentAsString();

        User createdUser = objectMapper.readValue(response, User.class);

        mockMvc.perform(get("/users/{id}", createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdUser.getId()));
    }

    @Test
    void updateUser_ReturnsUpdatedUser() throws Exception {
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andReturn().getResponse().getContentAsString();

        User createdUser = objectMapper.readValue(response, User.class);

        createdUser.setName("Updated Name");

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void addFriend_ShouldAddToFriendsList() throws Exception {
        // создаём user1
        String user1Json = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andReturn().getResponse().getContentAsString();

        User user1 = objectMapper.readValue(user1Json, User.class);

        // создаём user2
        validUser.setEmail("second@test.com");

        String user2Json = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andReturn().getResponse().getContentAsString();

        User user2 = objectMapper.readValue(user2Json, User.class);

        // добавляем в друзья
        mockMvc.perform(put("/users/{id}/friends/{friendId}", user1.getId(), user2.getId()))
                .andExpect(status().isOk());

        // проверяем, что друг появился
        mockMvc.perform(get("/users/{id}/friends", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(user2.getId()));
    }

    @Test
    void removeFriend_ShouldRemoveFromFriendsList() throws Exception {
        // создаём user1
        String user1Json = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andReturn().getResponse().getContentAsString();

        User user1 = objectMapper.readValue(user1Json, User.class);

        // создаём user2
        validUser.setEmail("second@test.com");

        String user2Json = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andReturn().getResponse().getContentAsString();

        User user2 = objectMapper.readValue(user2Json, User.class);

        // добавляем в друзья
        mockMvc.perform(put("/users/{id}/friends/{friendId}", user1.getId(), user2.getId()))
                .andExpect(status().isOk());

        // удаляем из друзей
        mockMvc.perform(delete("/users/{id}/friends/{friendId}", user1.getId(), user2.getId()))
                .andExpect(status().isOk());

        // проверяем, что список друзей пуст
        mockMvc.perform(get("/users/{id}/friends", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getCommonFriends_ShouldReturnList() throws Exception {
        // создаём 3 пользователей
        User u1 = objectMapper.readValue(
                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validUser)))
                        .andReturn().getResponse().getContentAsString(),
                User.class);

        validUser.setEmail("u2@test.com");
        User u2 = objectMapper.readValue(
                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validUser)))
                        .andReturn().getResponse().getContentAsString(),
                User.class);

        validUser.setEmail("u3@test.com");
        User u3 = objectMapper.readValue(
                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validUser)))
                        .andReturn().getResponse().getContentAsString(),
                User.class);

        // делаем общего друга
        mockMvc.perform(put("/users/{id}/friends/{friendId}", u1.getId(), u3.getId()));
        mockMvc.perform(put("/users/{id}/friends/{friendId}", u2.getId(), u3.getId()));

        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", u1.getId(), u2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(u3.getId()));
    }
}