package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

public interface ItemService {
    ItemDto create(NewItemRequest request, Integer ownerId);

    ItemDto update(Integer itemId, UpdateItemRequest request, Integer ownerId);

    ItemDto get(Integer itemId);

    List<ItemDto> getUserItems(Integer ownerId);

    List<ItemDto> search(String text);

    void delete(Integer id);
}
