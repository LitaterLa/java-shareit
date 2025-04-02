package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {
    @Autowired
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private final ItemRequestService service;

    private NewItemRequestDto newRequest;
    private ItemRequestDto dto;
    private List<ItemRequestDto> requests;

    @BeforeEach
    void setUp() {
        newRequest = NewItemRequestDto.builder()
                .description("of great beauty")
                .build();
        dto = UtilTestDataClass.TestItemRequest.beautyRequest();
        requests = List.of(dto);
    }

    @Test
    @SneakyThrows
    void create() {
        when(service.create(any(NewItemRequestDto.class), anyInt()))
                .thenReturn(dto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(dto))
                        .header("X-Sharer-User-Id", dto.getUserId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.created", is(dto.getCreated().toString())))
                .andExpect(jsonPath("$.userId", is(dto.getUserId())))
                .andExpect(jsonPath("$.items", is(dto.getItems())));

        verify(service, times(1)).create(any(NewItemRequestDto.class), anyInt());

    }

    @SneakyThrows
    @Test
    void findUserRequests() {
        int userId = dto.getUserId();
        when(service.findUserRequests(anyInt()))
                .thenReturn(requests);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(dto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(dto.getDescription())))
                .andExpect(jsonPath("$[0].userId", is(dto.getUserId())))
                .andExpect(jsonPath("$[0].items", is(dto.getItems())))
                .andExpect(jsonPath("$[0].created", is(dto.getCreated().toString())));

        verify(service).findUserRequests(userId);
    }

    @SneakyThrows
    @Test
    void findAllOtherUsers() {
        int userId = dto.getUserId();
        when(service.findUserRequests(anyInt()))
                .thenReturn(requests);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(dto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(dto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(dto.getCreated().toString())))
                .andExpect(jsonPath("$[0].userId", is(dto.getUserId())))
                .andExpect(jsonPath("$[0].items", is(dto.getItems())));

        verify(service).findUserRequests(userId);
    }

    @SneakyThrows
    @Test
    void getRequestById() {
        when(service.getRequestById(anyInt()))
                .thenReturn(dto);

        Integer id = dto.getId();
        mvc.perform(get("/requests/{id}", id)
                        .header("X-Sharer-User-Id", dto.getUserId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.created", is(dto.getCreated().toString())))
                .andExpect(jsonPath("$.userId", is(dto.getUserId())))
                .andExpect(jsonPath("$.items", is(dto.getItems())));

        verify(service).getRequestById(dto.getId());
    }

    @SneakyThrows
    @Test
    void deleteRequest() {
        int requestId = dto.getId();

        mvc.perform(delete("/requests/{requestId}", requestId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(service, times(1)).deleteItemRequest(requestId);
    }
}