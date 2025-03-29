package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingPeriod;
import ru.practicum.shareit.item.CommentDto;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDtoCommentBooking {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Integer id;
    String name;
    String description;
    Boolean available;
    Integer ownerId;
    Integer requestId;
    BookingPeriod lastBooking;
    BookingPeriod nextBooking;
    List<CommentDto> comments;
}

