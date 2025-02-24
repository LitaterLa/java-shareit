package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto create(NewItemRequest request, Integer ownerId) {
        userService.get(ownerId);
        Item item = itemMapper.toItem(ownerId, request);
        itemRepository.create(item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Integer itemId, UpdateItemRequest request, Integer ownerId) {
        Item item = itemRepository.get(itemId).orElseThrow(() -> new NotFoundException("item not found"));
        if (!(item.getOwnerId().equals(ownerId))) {
            throw new NotFoundException("Wrong owner ID");
        }
        return itemMapper.toItemDto(itemRepository.update(itemMapper.updateItem(request, item)));
    }

    @Override
    public ItemDto get(Integer itemId) {
        return itemRepository.get(itemId)
                .map(itemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("item not found"));
    }

    @Override
    public List<ItemDto> getUserItems(Integer userId) {
        List<ItemDto> items = itemRepository.getUserItems(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        if (items.size() == 0) {
            throw new NotFoundException("No matching results for user");
        }
        return items;
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        itemRepository.get(id);
        itemRepository.delete(id);
    }
}
