package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.UtilTestDataClass;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCommentBooking;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItem;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {
    @Autowired
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private final ItemService service;

    private ItemDto dto;
    private List<ItemDto> items;
    private ItemDtoCommentBooking itemDtoCommentBooking;
    private ItemDtoCommentBooking anotherItemDtoCommentBooking;
    private List<ItemDtoCommentBooking> dtoList;
    private CommentDto commentDto;


    @BeforeEach
    void setUp() {
        dto = UtilTestDataClass.TestItem.scarf();
        items = List.of(dto);
        itemDtoCommentBooking = UtilTestDataClass.TestItemCommentBooking.createItemDtoCommentBooking();
        anotherItemDtoCommentBooking = UtilTestDataClass.TestItemCommentBooking.createAnotherItemDtoCommentBooking();
        dtoList = List.of(itemDtoCommentBooking, anotherItemDtoCommentBooking);
        commentDto = UtilTestDataClass.TestComment.scarfComment();
    }

    @Test
    @SneakyThrows
    void createItem() {
        when(service.create(any(NewItemRequest.class), anyInt()))
                .thenReturn(dto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(dto))
                        .header("X-Sharer-User-Id", dto.getOwnerId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.available", is(dto.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(dto.getOwnerId())));


        verify(service, times(1)).create(any(NewItemRequest.class), anyInt());
    }


    @SneakyThrows
    @Test
    void testCreateComment() {
        when(service.createComment(any(NewCommentRequest.class), anyInt(), anyInt()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", commentDto.getItemId())
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", UtilTestDataClass.TestUser.rome().getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.itemId", is(commentDto.getItemId())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated())));


        verify(service, times(1)).createComment(any(NewCommentRequest.class), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void update() {
        when(service.update(anyInt(), any(), anyInt()))
                .thenReturn(dto);

        mvc.perform(patch("/items/{id}", dto.getId())
                        .content(mapper.writeValueAsString(dto))
                        .header("X-Sharer-User-Id", dto.getOwnerId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.available", is(dto.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(dto.getOwnerId())));

        verify(service, times(1)).update(anyInt(), any(UpdateItem.class), anyInt());
    }

    @SneakyThrows
    @Test
    void getItem() {
        when(service.get(anyInt(), anyInt()))
                .thenReturn(itemDtoCommentBooking);

        Integer id = itemDtoCommentBooking.getId();
        mvc.perform(get("/items/{id}", id)
                        .header("X-Sharer-User-Id", itemDtoCommentBooking.getOwnerId())
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoCommentBooking.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDtoCommentBooking.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoCommentBooking.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoCommentBooking.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(itemDtoCommentBooking.getOwnerId())));

        verify(service).get(itemDtoCommentBooking.getId(), itemDtoCommentBooking.getOwnerId());
    }

    @SneakyThrows
    @Test
    void getUserItems() {
        when(service.getUserItems(anyInt()))
                .thenReturn(dtoList);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", itemDtoCommentBooking.getOwnerId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoCommentBooking.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoCommentBooking.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoCommentBooking.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoCommentBooking.getAvailable())))
                .andExpect(jsonPath("$[0].ownerId", is(itemDtoCommentBooking.getOwnerId())))

                .andExpect(jsonPath("$[1].id", is(anotherItemDtoCommentBooking.getId()), Integer.class))
                .andExpect(jsonPath("$[1].name", is(anotherItemDtoCommentBooking.getName())))
                .andExpect(jsonPath("$[1].description", is(anotherItemDtoCommentBooking.getDescription())))
                .andExpect(jsonPath("$[1].available", is(anotherItemDtoCommentBooking.getAvailable())))
                .andExpect(jsonPath("$[1].ownerId", is(anotherItemDtoCommentBooking.getOwnerId())));

        verify(service).getUserItems(itemDtoCommentBooking.getOwnerId());
    }

    @Test
    @SneakyThrows
    void getItem_shouldReturnNotFound_whenItemNotExists() {
        when(service.get(anyInt(), anyInt()))
                .thenThrow(new NotFoundException("Item not found"));

        mvc.perform(get("/items/{id}", 999)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());

        verify(service).get(999, 1);
    }

    @SneakyThrows
    @Test
    void search() {
        when(service.search(anyString(), anyInt(), anyInt()))
                .thenReturn(items);

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", dto.getOwnerId())
                        .param("text", "scarf")
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(dto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(dto.getName())))
                .andExpect(jsonPath("$[0].description", is(dto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(dto.getAvailable())))
                .andExpect(jsonPath("$[0].ownerId", is(dto.getOwnerId())));

        verify(service).search(anyString(), anyInt(), anyInt());

    }

    @SneakyThrows
    @Test
    void deleteItem() {
        int id = dto.getId();

        mvc.perform(delete("/items/{id}", id)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(service, times(1)).delete(dto.getId());
    }
}