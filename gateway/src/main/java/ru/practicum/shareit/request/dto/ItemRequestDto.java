package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private Integer id;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate created;
    private Integer userId;
    private List<ItemDto> items;
}
