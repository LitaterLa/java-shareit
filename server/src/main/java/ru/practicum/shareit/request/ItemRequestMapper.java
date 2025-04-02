package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface ItemRequestMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "description", source = "dto.description")
    ItemRequest toNewModel(User user, NewItemRequestDto dto);

    @Mapping(target = "id", source = "request.id")
    @Mapping(target = "userId", source = "request.user.id")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "created", source = "request.created")
    @Mapping(target = "items", source = "itemsDto")
    ItemRequestDto toDto(ItemRequest request, List<ItemDto> itemsDto);

    default List<ItemRequestDto> toListDto(List<ItemRequest> requests, List<ItemDto> items) {
        Map<Integer, List<ItemDto>> itemsByRequestId = items.stream()
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return requests.stream()
                .map(request -> {
                    List<ItemDto> requestItems = itemsByRequestId.getOrDefault(request.getId(), List.of());
                    return toDto(request, requestItems);
                })
                .collect(Collectors.toList());
    }

}
