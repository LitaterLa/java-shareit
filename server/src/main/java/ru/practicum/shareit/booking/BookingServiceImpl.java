package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.IllegalAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper mapper;


    @Override
    @Transactional
    public BookingDto createBookingRequest(Integer bookerId, NewBookingRequest bookingRequest) {
        User user = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User " + bookerId + " was not found"));

        Item item = itemRepository.findById(bookingRequest.getItemId())
                .orElseThrow(() -> new NotFoundException("Item id " + bookingRequest.getItemId() + " not found"));
        if (!item.getAvailable()) {
            throw new BadRequestException("Cannot book unavailable item");
        }

        Booking booking = mapper.toBooking(user, item, bookingRequest);
        if (booking.getStart().equals(booking.getEnd())) {
            throw new BadRequestException("Booking start shouldn't be equal to booking end");
        }
        if (booking.getEnd().isBefore(LocalDateTime.now()) || booking.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Booking start or end cannot be in the past");
        }
        bookingRepository.save(booking);
        return mapper.toBookingDto(booking);
    }


    @Override
    @Transactional
    public BookingDto updateBookingStatus(Integer bookingId, Integer ownerId, boolean processable) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking id " + bookingId + " not found"));
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new IllegalAccessException("Not booking's owner");
        }
        if (processable) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        return mapper.toBookingDto(booking);
    }

    @Override
    public BookingDto findById(Integer id, Integer userId) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new NotFoundException("Booking id " + id + " not found"));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new IllegalAccessException("Not booker or owner");
        }
        return mapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findAllByUserId(Integer userId, String state) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Booker with id " + userId + " not found"));
        try {
            State state1 = Enum.valueOf(State.class, state.toUpperCase());
            List<Booking> bookings = stateSwitch(state1, bookingRepository.findAllByBookerId(userId));
            return bookings.stream()
                    .sorted(Comparator.comparing(Booking::getStart).reversed())
                    .map(mapper::toBookingDto)
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("State " + state + " not found");
        }
    }

    @Override
    public List<BookingDto> findAllByOwnerId(Integer ownerId, String state) {
        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Owner with id " + ownerId + " not found"));

        try {
            State state1 = Enum.valueOf(State.class, state.toUpperCase());
            List<Booking> bookings = stateSwitch(state1, bookingRepository.findAllByItemOwnerId(ownerId));
            return bookings.stream()
                    .sorted(Comparator.comparing(Booking::getStart).reversed())
                    .map(mapper::toBookingDto)
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("State " + state + " not found");
        }
    }

    private List<Booking> stateSwitch(State state1, List<Booking> bookings) {
        return switch (state1) {
            case PAST -> bookings.stream().filter(b -> b.getEnd().isBefore(LocalDateTime.now())).toList();
            case FUTURE -> bookings.stream().filter(b -> b.getStart().isAfter(LocalDateTime.now())).toList();
            case CURRENT -> bookings.stream()
                    .filter(b -> b.getEnd().isAfter(LocalDateTime.now()))
                    .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                    .toList();
            case WAITING -> bookings.stream().filter(b -> b.getStatus().equals(Status.WAITING)).toList();
            case REJECTED -> bookings.stream().filter(b -> b.getStatus().equals(Status.REJECTED)).toList();
            case ALL -> bookings;
        };
    }
}
