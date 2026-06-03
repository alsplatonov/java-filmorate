package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class UpdateUserRequest {

    @NotNull(message = "id не может быть пустым")
    private Long id;

    @Email(message = "Некорректный email")
    private String email;

    @Pattern(regexp = "^\\S+$", message = "Логин не должен содержать пробелы")
    private String login;

    private String name;

    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();

    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }

    public boolean hasLogin() {
        return login != null && !login.isBlank();
    }

    public boolean hasBirthday() {
        return birthday != null;
    }
}