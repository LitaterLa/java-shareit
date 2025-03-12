package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.validation.Create;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)

public class NewUserRequest {
    @NotBlank(groups = Create.class)
    @NotNull
    String name;
    @NotBlank(groups = Create.class)
    @Email(groups = Create.class)
    String email;
}
