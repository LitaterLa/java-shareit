package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCommentBooking;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItem;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private static final String HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestBody NewItemRequest request,
                          @RequestHeader(HEADER) Integer ownerId) {
        log.info("Creating item name={}", request.getName());
        return itemService.create(request, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto create(@RequestBody NewCommentRequest commentRequest,
                             @PathVariable Integer itemId,
                             @RequestHeader(HEADER) Integer userId) {
        return itemService.createComment(commentRequest, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Integer itemId,
                          @RequestBody UpdateItem request,
                          @RequestHeader(HEADER) Integer userId) {
        log.info("Updating item id={}", itemId);
        return itemService.update(itemId, request, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoCommentBooking get(@PathVariable Integer itemId,
                                     @RequestHeader(HEADER) Integer userId) {
        log.info("Getting item id={}", itemId);
        return itemService.get(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoCommentBooking> getUserItems(@RequestHeader(HEADER) Integer ownerId) {
        log.info("Getting items of user id={}", ownerId);
        return itemService.getUserItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(value = "text") String text,
                                @RequestParam(defaultValue = "0") int from,
                                @RequestParam(defaultValue = "10") int size) {
        log.info("Searching for items with {}", text);
        return itemService.search(text, from, size);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Integer itemId) {
        log.info("Deleting item id={}", itemId);
        itemService.delete(itemId);
    }
}
