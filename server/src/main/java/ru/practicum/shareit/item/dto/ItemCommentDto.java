package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.CommentDto;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
public class ItemCommentDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Integer id;
    String name;
    String description;
    Boolean available;
    Integer ownerId;
    Integer requestId;
    List<CommentDto> comments;
}
