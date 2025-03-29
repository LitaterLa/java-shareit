package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    @Autowired
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private final BookingService service;

    private NewBookingRequest newRequest;
    private BookingDto dto;
    private UpdateBookingRequest update;
    private List<BookingDto> bookings;

    @BeforeEach
    void setUp() {
        newRequest = NewBookingRequest.builder()
                .itemId(1)
                .start(LocalDateTime.of(2025, 1, 1, 10, 0))
                .end(LocalDateTime.of(2025, 1, 5, 12, 0))
                .build();
        dto = UtilTestDataClass.TestBooking.approvedBeretBooking();
        update = new UpdateBookingRequest();
        update.setStatus("APPROVED");
        bookings = List.of(dto);
    }

    @Test
    @SneakyThrows
    void createBookingRequest() {
        when(service.createBookingRequest(anyInt(), any(NewBookingRequest.class)))
                .thenReturn(dto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(dto))
                        .header("X-Sharer-User-Id", dto.getBooker().getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Integer.class))
                .andExpect(jsonPath("$.item.id", is(dto.getItem().getId())))
                .andExpect(jsonPath("$.item.ownerId", is(dto.getItem().getOwnerId())))
                .andExpect(jsonPath("$.item.description", is(dto.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(dto.getItem().getAvailable())));

        verify(service, times(1)).createBookingRequest(anyInt(), any(NewBookingRequest.class));
    }


    @Test
    @SneakyThrows
    void processBookingRequest() {
        when(service.updateBookingStatus(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(dto);

        mvc.perform(patch("/bookings/{id}", dto.getId())
                        .content(mapper.writeValueAsString(update))
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", dto.getBooker().getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(dto.getStatus())))
                .andExpect(jsonPath("$.item.id", is(dto.getItem().getId())))
                .andExpect(jsonPath("$.item.ownerId", is(dto.getItem().getOwnerId())))
                .andExpect(jsonPath("$.item.description", is(dto.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(dto.getItem().getAvailable())));

        verify(service, times(1)).updateBookingStatus(anyInt(), anyInt(), anyBoolean());
    }

    @Test
    @SneakyThrows
    void findById() {
        when(service.findById(anyInt(), anyInt()))
                .thenReturn(dto);

        Integer id = dto.getId();
        mvc.perform(get("/bookings/{id}", id)
                        .header("X-Sharer-User-Id", dto.getBooker().getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(dto.getStatus())));

        verify(service).findById(dto.getId(), dto.getBooker().getId());
    }

    @Test
    @SneakyThrows
    void findAllByUserId() {
        int userId = dto.getBooker().getId();
        when(service.findAllByUserId(anyInt(), anyString()))
                .thenReturn(bookings);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(dto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].status", is(dto.getStatus())));


        verify(service).findAllByUserId(userId,"ALL");
    }

    @Test
    @SneakyThrows
    void findAllByOwnerId() {
        int userId = dto.getBooker().getId();
        when(service.findAllByUserId(anyInt(), anyString()))
                .thenReturn(bookings);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(dto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].status", is(dto.getStatus())));


        verify(service).findAllByUserId(userId,"ALL");
    }
}