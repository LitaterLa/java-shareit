package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.util.List;

public interface BookingService {
    BookingDto createBookingRequest(Integer bookerId, NewBookingRequest booking);

    BookingDto updateBookingStatus(Integer bookingId, Integer ownerId, boolean processable);

    BookingDto findById(Integer id, Integer userId);

    List<BookingDto> findAllByUserId(Integer userId, String state);

    List<BookingDto> findAllByOwnerId(Integer ownerId, String state);

}
