package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestBody NewItemRequestDto dto,
                                 @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("creating new item request for user{}", userId);
        return itemRequestService.create(dto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> findUserRequests(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("finding item requests for user{}", userId);
        return itemRequestService.findUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllOtherUsers(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "15") Integer size) {
        log.info("finding all item requests");
        return itemRequestService.findAll(userId, from, size);
    }

    @GetMapping("/{id}")
    public ItemRequestDto getRequestById(@PathVariable Integer id) {
        log.info("finding item requests id{}", id);
        return itemRequestService.getRequestById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        itemRequestService.deleteItemRequest(id);
    }


}
