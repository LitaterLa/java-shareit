package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private static final String HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBookingRequest(@RequestBody NewBookingRequest booking,
                                           @RequestHeader(HEADER) Integer bookerId) {
        log.info("Creating new booking itemId {}", booking.getItemId());
        log.info("Creating new booking item start{}", booking.getStart());
        log.info("Creating new booking item start{}", booking.getEnd());
        return bookingService.createBookingRequest(bookerId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto processBookingRequest(@PathVariable Integer bookingId,
                                            @RequestHeader(HEADER) Integer ownerId,
                                            @RequestParam("approved") Boolean processable) {
        log.info("Processing booking id{}", bookingId);
        return bookingService.updateBookingStatus(bookingId, ownerId, processable);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@PathVariable Integer bookingId,
                               @RequestHeader(HEADER) Integer userId) {
        log.info("Looking for booking id{}", bookingId);
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findAllByUserId(@RequestHeader(HEADER) Integer userId,
                                            @RequestParam(value = "status", defaultValue = "ALL") String state) {
        log.info("Looking for booking made by user{}", userId);
        return bookingService.findAllByUserId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwnerId(@RequestHeader(HEADER) Integer ownerId,
                                             @RequestParam(value = "status", defaultValue = "ALL") String state) {
        log.info("Looking for booking items owned by user{}", ownerId);
        return bookingService.findAllByOwnerId(ownerId, state);
    }


}
