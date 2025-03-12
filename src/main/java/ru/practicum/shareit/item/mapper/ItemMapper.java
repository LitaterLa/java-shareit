package ru.practicum.shareit.item.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingPeriod;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCommentBooking;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
@Mapper(componentModel = "spring", uses = {UserMapper.class, CommentMapper.class})
public interface ItemMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "name", source = "itemRequest.name")
    Item toItem(User owner, NewItemRequest itemRequest);

    @Mapping(target = "ownerId", source = "item.owner.id")
    ItemDto toItemDto(Item item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Item updateItem(UpdateItemRequest request, @MappingTarget Item item);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "name", source = "item.name")
    @Mapping(target = "description", source = "item.description")
    @Mapping(target = "available", source = "item.available")
    @Mapping(target = "ownerId", source = "item.owner.id")
    @Mapping(target = "lastBooking", source = "last")
    @Mapping(target = "nextBooking", source = "next")
    ItemDtoCommentBooking toItemDtoBooking(Item item, BookingPeriod last, BookingPeriod next, List<Comment> comments);

//    @Mapping(target = "ownerId", source = "item.owner.id")
//    ItemCommentDto toItemCommentDto(Item item, List<Comment> comments);

}
