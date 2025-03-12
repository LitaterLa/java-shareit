package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ManualItemRepository {
    private final Map<Integer, Item> items;
    private Integer counter = 0;


    public Item create(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }

    public Item update(Item item) {
        Item newItem = items.get(item.getId());
        if (item.getAvailable() != null) {
            newItem.setAvailable(item.getAvailable());
        }
        if (item.getName() != null) {
            newItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            newItem.setDescription(item.getDescription());
        }
        items.put(newItem.getId(), item);
        return newItem;
    }


    public Optional<Item> get(Integer itemId) {
        return Optional.ofNullable(items.get(itemId));
    }


    public List<Item> getUserItems(Integer userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Item> search(String text) {
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    public void delete(Integer id) {
        items.remove(id);
    }

    private Integer generateId() {
        return ++counter;
    }
}
