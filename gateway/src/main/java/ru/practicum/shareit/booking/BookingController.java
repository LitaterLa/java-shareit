package ru.practicum.shareit.booking;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.NewBookingRequest;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBookingRequest(@RequestBody @Validated NewBookingRequest booking,
                                                       @RequestHeader("X-Sharer-User-Id") Integer bookerId) {
        log.info("Creating new booking itemId {}", booking.getItemId());
        log.info("Creating new booking item start{}", booking.getStart());
        log.info("Creating new booking item end{}", booking.getEnd());
        return bookingClient.bookItem(bookerId, booking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> processBookingRequest(@PathVariable Integer bookingId,
                                                        @RequestHeader("X-Sharer-User-Id") Integer ownerId,
                                                        @RequestParam("approved") Boolean processable) {
        log.info("Processing booking id{}", bookingId);
        return bookingClient.updateBookingState(bookingId, ownerId, processable);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable Integer bookingId,
                                             @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Looking for booking id{}", bookingId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                  @RequestParam(value = "status", defaultValue = "ALL") String state,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Looking for booking made by user{}", userId);
        BookingState bookingStatus = BookingState.from(state).orElseThrow(() -> new IllegalArgumentException("Incorrect booking status"));
        return bookingClient.getBookings(userId, bookingStatus, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Integer ownerId,
                                                   @RequestParam(value = "status", defaultValue = "ALL") String state,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Looking for booking items owned by user{}", ownerId);
        BookingState bookingStatus = BookingState.from(state).orElseThrow(() -> new IllegalArgumentException("Incorrect booking status"));
        return bookingClient.getOwnerBookings(ownerId, bookingStatus, from, size);
    }


}
