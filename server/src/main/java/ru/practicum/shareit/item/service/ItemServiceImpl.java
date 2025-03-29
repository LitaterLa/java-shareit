package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingPeriod;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.NewCommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCommentBooking;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItem;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto create(NewItemRequest request, Integer ownerId) {
        User owner = userMapper.toUser(userService.get(ownerId));
        Item item = itemMapper.toItem(owner, request);
        item.setOwner(owner);
        Item saved = itemRepository.save(item);
        return itemMapper.toItemDto(saved);
    }

    @Transactional
    public CommentDto createComment(NewCommentRequest commentDto, Integer itemId, Integer userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        LocalDateTime now = LocalDateTime.now();

        Boolean hasPastBooking = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(userId, itemId, now);
        if (Boolean.FALSE.equals(hasPastBooking)) {
            throw new BadRequestException("You can only comment after renting this item and the rental period is over.");
        }
        Comment comment = commentMapper.toComment(commentDto, item, user);
        comment.setCreated(now);
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }


    @Override
    public ItemDto update(Integer itemId, UpdateItem request, Integer ownerId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("item not found"));
        if (!(item.getOwner().getId().equals(ownerId))) {
            throw new NotFoundException("Wrong owner ID");
        }
        return itemMapper.toItemDto(itemRepository.save(itemMapper.updateItem(request, item)));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDtoCommentBooking get(Integer itemId, Integer userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("item not found"));
        BookingPeriod next = null;
        BookingPeriod last = null;
        if (userId.equals(item.getOwner().getId())) {
            next = bookingRepository.findNextBookingByItemId(itemId, LocalDateTime.now()).orElse(null);
            last = bookingRepository.findLastBookingByItemId(itemId, LocalDateTime.now()).orElse(null);
        }
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        return itemMapper.toItemDtoBooking(item, next, last, comments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoCommentBooking> getUserItems(Integer userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        if (items.isEmpty()) {
            throw new NotFoundException("No matching results for user id=" + userId);
        }
        List<Integer> itemIds = items.stream().map(Item::getId).toList();

        List<Booking> bookings = bookingRepository.findByItemIdIn(itemIds);

        List<Comment> comments = commentRepository.findAllByItemIds(itemIds);

        Map<Integer, List<Booking>> bookingsByItem = bookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        Map<Integer, List<Comment>> commentsByItem = comments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        return items.stream()
                .map(item -> {
                    List<Booking> itemBookings = bookingsByItem.getOrDefault(item.getId(), List.of());

                    Booking last = getLast(itemBookings, item.getId());
                    Booking next = getNext(itemBookings, item.getId());

                    BookingPeriod lastPeriod = (last != null) ? new BookingPeriod(last.getStart(), last.getEnd()) : null;
                    BookingPeriod nextPeriod = (next != null) ? new BookingPeriod(next.getStart(), next.getEnd()) : null;

                    return itemMapper.toItemDtoBooking(
                            item,
                            lastPeriod,
                            nextPeriod,
                            commentsByItem.getOrDefault(item.getId(), List.of())
                    );
                })
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String text, int from, int size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.findAllByNameOrDescriptionContaining(text, PageRequest.of(from, size)).stream()
                .filter(Item::getAvailable)
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Item to delete was not found"));
        itemRepository.delete(item);
    }

    private Booking getLast(List<Booking> bookings, Integer itemId) {
        return bookings.stream()
                .filter(booking -> booking.getItem().getId().equals(itemId))
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);
    }

    private Booking getNext(List<Booking> bookings, Integer itemId) {
        LocalDateTime now = LocalDateTime.now();

        return bookings.stream()
                .filter(booking -> booking.getItem().getId().equals(itemId))
                .filter(booking -> booking.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
    }
}
