package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.NewCommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCommentBooking;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItem;

import java.util.List;

public interface ItemService {
    ItemDto create(NewItemRequest request, Integer ownerId);

    ItemDto update(Integer itemId, UpdateItem request, Integer ownerId);

    ItemDtoCommentBooking get(Integer itemId, Integer userId);

    List<ItemDtoCommentBooking> getUserItems(Integer ownerId);

    List<ItemDto> search(String text, int from, int size);

    void delete(Integer id);

    CommentDto createComment(NewCommentRequest commentDto, Integer itemId, Integer userId);

}
