package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.validation.Create;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewItemRequest {
    @NotBlank(groups = Create.class)
    String name;
    @NotBlank(groups = Create.class)
    String description;
    @NotNull(groups = Create.class)
    Boolean available;
}
