package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item create(Item item);

    Item update(Item item);

    Optional<Item> get(Integer itemId);

    List<Item> getUserItems(Integer userId);

    List<Item> search(String text);

    void delete(Integer itemId);

}
