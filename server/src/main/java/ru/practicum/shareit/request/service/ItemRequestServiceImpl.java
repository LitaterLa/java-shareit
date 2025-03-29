package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper mapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemRequestDto create(NewItemRequestDto dto, Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User was not found"));
        ItemRequest itemRequest = mapper.toNewModel(user, dto);
        itemRequest.setCreated(LocalDateTime.now());
        repository.save(itemRequest);
        return mapper.toDto(itemRequest, new ArrayList<>());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> findUserRequests(Integer userId) {
        List<ItemRequest> itemRequests = repository.findAllByUserId(userId);
        return toListItemRequestDto(itemRequests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> findAll(Integer userId, Integer from, Integer size) {
        PageRequest pageable = PageRequest.of(from, size, Sort.by("created").descending());
        List<ItemRequest> itemRequests = repository.findByUserIdNot(userId, pageable);
        return toListItemRequestDto(itemRequests);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getRequestById(Integer id) {
        ItemRequest itemRequest = repository.findById(id).orElseThrow(() -> new NotFoundException("Not found item request id " + id));
        List<Item> items = itemRepository.findAllByRequestId(id);
        List<ItemDto> itemsDto = items.stream().map(itemMapper::toItemDto).toList();
        return mapper.toDto(itemRequest, itemsDto);
    }

    @Override
    @Transactional
    public void deleteItemRequest(Integer id) {
        repository.deleteById(id);
    }

    private List<ItemRequestDto> toListItemRequestDto(List<ItemRequest> itemRequests) {
        List<Integer> requestIds = itemRequests.stream().map(ItemRequest::getId).toList();
        List<Item> itemsByIds = itemRepository.findAllByRequestIds(requestIds);
        List<ItemDto> itemsDto = itemsByIds.stream().map(itemMapper::toItemDto).toList();

        return mapper.toListDto(itemRequests, itemsDto);
    }
}
