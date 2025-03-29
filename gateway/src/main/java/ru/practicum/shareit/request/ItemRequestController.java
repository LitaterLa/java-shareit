package ru.practicum.shareit.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.NewItemRequestDto;


@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient client;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestBody NewItemRequestDto dto,
                                         @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("creating new item request for user{}", userId);
        return client.create(dto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findUserRequests(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("finding item requests for user{}", userId);
        return client.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllOtherUsers(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                    @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("getting item requests userId = {}, from = {}, size = {}", userId, from, size);
        return client.getAllRequests(userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRequestById(@PathVariable Integer id) {
        log.info("finding item requests id{}", id);
        return client.getRequestById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        log.info("deleting item request {}", id);
        return client.deleteRequest(id);
    }


}
