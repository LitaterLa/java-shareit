package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

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
    @Mapping(target = "created", source = "created")
    CommentDto toDto(Comment comment);

}
