package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public ItemDto create(NewItemRequest request, Integer ownerId) {
        Item item = ItemMapper.mapToItem(ownerId, request);
        itemRepository.create(item);
        return ItemMapper.mapToDto(item);
    }

    @Override
    public ItemDto update(Integer itemId, UpdateItemRequest request, Integer ownerId) {
        Item item = itemRepository.get(itemId).orElseThrow(() -> new NotFoundException("item not found"));
        if (!(item.getOwnerId().equals(ownerId))) {
            throw new ValidationException("Wrong owner ID");
        }
        return ItemMapper.mapToDto(itemRepository.update(ItemMapper.updateItem(item, request)));
    }

    @Override
    public ItemDto get(Integer itemId) {
        return itemRepository.get(itemId)
                .map(ItemMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("item not found"));
    }

    @Override
    public List<ItemDto> getUserItems(Integer userId) {
        List<ItemDto> items = itemRepository.getUserItems(userId).stream()
                .map(ItemMapper::mapToDto)
                .collect(Collectors.toList());
        if (items.size() == 0) {
            throw new NotFoundException("No matching results for user");
        }
        return items;
    }

    @Override
    public List<ItemDto> search(String text) {
        List<ItemDto> items = itemRepository.search(text).stream()
                .map(ItemMapper::mapToDto)
                .collect(Collectors.toList());
        if (items.size() == 0) {
            throw new NotFoundException("No matching search results");
        }
        return items;
    }

    @Override
    public void delete(Integer id) {
        itemRepository.get(id);
        itemRepository.delete(id);
    }
}
