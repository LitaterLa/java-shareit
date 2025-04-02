package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(NewItemRequestDto dto, Integer userId);

    List<ItemRequestDto> findUserRequests(Integer userId);

    List<ItemRequestDto> findAll(Integer userId, Integer from, Integer size);

    ItemRequestDto getRequestById(Integer id);

    void deleteItemRequest(Integer id);

}
