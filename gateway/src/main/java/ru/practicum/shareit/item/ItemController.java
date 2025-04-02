package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;


@RestController
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private static final String HEADER = "X-Sharer-User-Id";
    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> create(@Validated @RequestBody NewItemRequest request,
                                         @RequestHeader(HEADER) Integer ownerId) {
        log.info("Creating item name={}", request.getName());
        return client.createItem(request, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> create(@Validated @RequestBody NewCommentRequest commentRequest,
                                         @PathVariable Integer itemId,
                                         @RequestHeader(HEADER) Integer userId) {
        return client.createComment(commentRequest, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable Integer itemId,
                                         @RequestBody UpdateItemRequest request,
                                         @RequestHeader(HEADER) Integer userId) {
        log.info("Updating item id={}", itemId);
        return client.update(itemId, request, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@PathVariable Integer itemId,
                                      @RequestHeader(HEADER) Integer userId) {
        log.info("Getting item id={}", itemId);
        return client.get(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(HEADER) Integer ownerId) {
        log.info("Getting items of user id={}", ownerId);
        return client.getUserItems(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam(value = "text") String text,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        log.info("Searching for items with {}", text);
        return client.search(text, from, size);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> delete(@PathVariable Integer itemId) {
        log.info("Deleting item id={}", itemId);
        return client.delete(itemId);
    }
}
