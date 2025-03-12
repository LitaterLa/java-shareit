package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "item", source = "item")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", source = "user")
    @Mapping(target = "created", ignore = true)
    Comment toComment(NewCommentRequest dto, Item item, User user);

    @Mapping(target = "authorName", source = "author.name")
    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "created", source = "created", qualifiedByName = "instantToString")
    CommentDto toDto(Comment comment);

    @Named("mapStringToInstant")
    default Instant mapStringToInstant(String instant) {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime localDateTime = LocalDateTime.parse(instant, formatter);
        return localDateTime.atZone(ZoneId.of("UTC")).toInstant();
    }

    @Named("instantToString")
    default String instantToString(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.of("UTC"));
        return formatter.format(instant);
    }


}
