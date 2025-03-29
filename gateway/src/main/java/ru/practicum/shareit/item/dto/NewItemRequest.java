package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewItemRequest {
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    Boolean available;

    Integer requestId;
}
