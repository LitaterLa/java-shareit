package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPeriod;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.NewCommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCommentBooking;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItem;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class MappersTest {

    @Autowired
    private BookingMapper bookingMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private ItemRequestMapper itemRequestMapper;
    @Autowired
    private UserMapper userMapper;

    // UserMapper tests

    @Test
    void usersMapper_ShouldMapCorrectly() {
        toUserDto_ShouldMapAllFields();
        toUserDto_ShouldReturnNullForNullInput();
        toUserFromNewRequest_ShouldMapAllFields();
        toUserFromNewRequest_ShouldReturnNullForNullInput();
        toUserFromDto_ShouldMapAllFields();
        toUserFromDto_ShouldReturnNullForNullInput();
        updateUserFields_ShouldUpdateNonNullFields();
        updateUserFields_ShouldNotUpdateNullFields();
        updateUserFields_ShouldDoNothingForNullRequest();
    }

    private void toUserDto_ShouldMapAllFields() {
        User user = User.builder()
                .id(1)
                .name("Test User")
                .email("test@test.com")
                .build();

        UserDto dto = userMapper.toUserDto(user);

        assertAll(
                () -> assertThat(dto.getId(), equalTo(user.getId())),
                () -> assertThat(dto.getName(), equalTo(user.getName())),
                () -> assertThat(dto.getEmail(), equalTo(user.getEmail()))
        );
    }

    private void toUserDto_ShouldReturnNullForNullInput() {
        assertThat(userMapper.toUserDto(null), nullValue());
    }

    private void toUserFromNewRequest_ShouldMapAllFields() {
        NewUserRequest request = new NewUserRequest("New User", "new@test.com");

        User user = userMapper.toUser(request);

        assertAll(
                () -> assertThat(user.getName(), equalTo(request.getName())),
                () -> assertThat(user.getEmail(), equalTo(request.getEmail())),
                () -> assertThat(user.getId(), nullValue())
        );
    }

    private void toUserFromNewRequest_ShouldReturnNullForNullInput() {
        assertThat(userMapper.toUser((NewUserRequest) null), nullValue());
    }

    private void toUserFromDto_ShouldMapAllFields() {
        UserDto dto = new UserDto(1, "Dto User", "dto@test.com");

        User user = userMapper.toUser(dto);

        assertAll(
                () -> assertThat(user.getId(), equalTo(dto.getId())),
                () -> assertThat(user.getName(), equalTo(dto.getName())),
                () -> assertThat(user.getEmail(), equalTo(dto.getEmail()))
        );
    }

    private void toUserFromDto_ShouldReturnNullForNullInput() {
        assertThat(userMapper.toUser((UserDto) null), nullValue());
    }


    private void updateUserFields_ShouldUpdateNonNullFields() {
        User user = User.builder()
                .id(1)
                .name("Original")
                .email("original@test.com")
                .build();

        UpdateUserRequest update = new UpdateUserRequest("Updated", "updated@test.com");
        userMapper.updateUserFields(update, user);

        assertAll(
                () -> assertThat(user.getName(), equalTo("Updated")),
                () -> assertThat(user.getEmail(), equalTo("updated@test.com"))
        );
    }

    private void updateUserFields_ShouldNotUpdateNullFields() {
        User user = User.builder()
                .id(1)
                .name("Original")
                .email("original@test.com")
                .build();

        UpdateUserRequest partialUpdate = new UpdateUserRequest(null, "updated@test.com");
        userMapper.updateUserFields(partialUpdate, user);

        assertAll(
                () -> assertThat(user.getName(), equalTo("Original")),
                () -> assertThat(user.getEmail(), equalTo("updated@test.com"))
        );
    }

    private void updateUserFields_ShouldDoNothingForNullRequest() {
        User user = User.builder()
                .id(1)
                .name("Original")
                .email("original@test.com")
                .build();

        userMapper.updateUserFields(null, user);

        assertAll(
                () -> assertThat(user.getName(), equalTo("Original")),
                () -> assertThat(user.getEmail(), equalTo("original@test.com"))
        );
    }


    // ItemMapper tests
    @Test
    void itemMapper_ShouldMapCorrectly() {
        User owner = new User(1, "owner", "owner@test.com");
        testItemMapping(owner);
        testItemDtoMapping(owner);
        testItemUpdateMapping(owner);
        testItemDtoBookingMapping(owner);
    }

    private void testItemMapping(User owner) {
        NewItemRequest newItemRequest = new NewItemRequest("item", "desc", true, 1);
        Item item = itemMapper.toItem(owner, newItemRequest);

        assertAll(
                () -> assertThat(item.getName(), equalTo("item")),
                () -> assertThat(item.getDescription(), equalTo("desc")),
                () -> assertThat(item.getAvailable(), equalTo(true)),
                () -> assertThat(item.getOwner(), equalTo(owner)),
                () -> assertThat(item.getRequestId(), equalTo(1))
        );
    }

    private void testItemDtoMapping(User owner) {
        Item item = new Item(1, "item", "desc", true, owner, 1);
        ItemDto itemDto = itemMapper.toItemDto(item);

        assertAll(
                () -> assertThat(itemDto.getId(), equalTo(item.getId())),
                () -> assertThat(itemDto.getName(), equalTo(item.getName())),
                () -> assertThat(itemDto.getDescription(), equalTo(item.getDescription())),
                () -> assertThat(itemDto.getAvailable(), equalTo(item.getAvailable())),
                () -> assertThat(itemDto.getOwnerId(), equalTo(owner.getId())),
                () -> assertThat(itemDto.getRequestId(), equalTo(item.getRequestId()))
        );
    }

    private void testItemUpdateMapping(User owner) {
        Item existingItem = new Item(1, "old", "old desc", false, owner, null);

        UpdateItem fullUpdate = new UpdateItem("new", "new desc", true);
        Item updatedItem = itemMapper.updateItem(fullUpdate, existingItem);

        assertAll(
                () -> assertThat(updatedItem.getName(), equalTo("new")),
                () -> assertThat(updatedItem.getDescription(), equalTo("new desc")),
                () -> assertThat(updatedItem.getAvailable(), equalTo(true))
        );
    }

    private void testItemDtoBookingMapping(User owner) {
        Item item = new Item(1, "item", "desc", true, owner, null);
        BookingPeriod lastBooking = new BookingPeriod(LocalDateTime.now().minusMonths(2L), LocalDateTime.now().minusMonths(1L));
        BookingPeriod nextBooking = new BookingPeriod(LocalDateTime.now().plusMonths(1L), LocalDateTime.now().plusMonths(4L));
        Comment comment = new Comment(1, "text", item, owner, LocalDateTime.now());

        ItemDtoCommentBooking dto = itemMapper.toItemDtoBooking(item, lastBooking, nextBooking, List.of(comment));

        assertAll(
                () -> assertThat(dto.getLastBooking(), equalTo(lastBooking)),
                () -> assertThat(dto.getNextBooking(), equalTo(nextBooking)),
                () -> assertThat(dto.getComments(), hasSize(1))
        );
    }

    // BookingMapper tests
    @Test
    void bookingMapper_ShouldMapCorrectly() {
        User booker = new User(2, "booker", "booker@test.com");
        Item item = new Item(1, "item", "desc", true, booker, null);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        testBookingMapping(booker, item, start, end);
        testBookingDtoMapping(booker, item, start, end);
        testCustomMappers(booker, item);
    }

    private void testBookingMapping(User booker, Item item, LocalDateTime start, LocalDateTime end) {
        NewBookingRequest bookingRequest = new NewBookingRequest(start, end, 1);
        Booking booking = bookingMapper.toBooking(booker, item, bookingRequest);

        assertAll(
                () -> assertThat(booking.getStatus(), equalTo(Status.WAITING)),
                () -> assertThat(booking.getBooker(), equalTo(booker)),
                () -> assertThat(booking.getStart(), equalTo(start)),
                () -> assertThat(booking.getEnd(), equalTo(end))
        );
    }

    private void testBookingDtoMapping(User booker, Item item, LocalDateTime start, LocalDateTime end) {
        Booking booking = Booking.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        BookingDto bookingDto = bookingMapper.toBookingDto(booking);

        assertAll(
                () -> assertThat(bookingDto.getId(), equalTo(booking.getId())),
                () -> assertThat(bookingDto.getStatus(), equalTo("APPROVED")),
                () -> assertThat(bookingDto.getStart(), equalTo(start)),
                () -> assertThat(bookingDto.getEnd(), equalTo(end))
        );
    }

    private void testCustomMappers(User booker, Item item) {
        assertAll(
                () -> assertThat(bookingMapper.customMapUser(null), nullValue()),
                () -> assertThat(bookingMapper.customMapItem(null), nullValue()),
                () -> assertThat(bookingMapper.customMapUser(booker), equalTo(booker)),
                () -> assertThat(bookingMapper.customMapItem(item).getId(), equalTo(item.getId()))
        );
    }

    // CommentMapper tests
    @Test
    void commentMapper_ShouldMapCorrectly() {
        User author = new User(3, "author", "author@test.com");
        Item item = new Item(2, "item2", "desc2", true, author, null);

        testCommentMapping(author, item);
        testCommentDtoMapping(author, item);
    }

    private void testCommentMapping(User author, Item item) {
        NewCommentRequest commentRequest = new NewCommentRequest("text");
        Comment comment = commentMapper.toComment(commentRequest, item, author);

        assertAll(
                () -> assertThat(comment.getText(), equalTo("text")),
                () -> assertThat(comment.getAuthor(), equalTo(author)),
                () -> assertThat(comment.getItem(), equalTo(item))
        );
    }

    private void testCommentDtoMapping(User author, Item item) {
        LocalDateTime created = LocalDateTime.now().minusHours(1);
        Comment comment = new Comment(1, "full text", item, author, created);
        CommentDto commentDto = commentMapper.toDto(comment);

        assertAll(
                () -> assertThat(commentDto.getId(), equalTo(comment.getId())),
                () -> assertThat(commentDto.getAuthorName(), equalTo(author.getName())),
                () -> assertThat(commentDto.getText(), equalTo(comment.getText())),
                () -> assertThat(commentDto.getItemId(), equalTo(item.getId()))
        );
    }

    // ItemRequestMapper tests
    @Test
    void itemRequestMapper_ShouldMapCorrectly() {
        User requester = new User(4, "requester", "requester@test.com");

        testItemRequestMapping(requester);
        testItemRequestDtoMapping(requester);
        testEdgeCases();
    }

    private void testItemRequestMapping(User requester) {
        NewItemRequestDto requestDto = new NewItemRequestDto("description");
        ItemRequest request = itemRequestMapper.toNewModel(requester, requestDto);

        assertAll(
                () -> assertThat(request.getDescription(), equalTo("description")),
                () -> assertThat(request.getUser(), equalTo(requester))
        );
    }

    private void testItemRequestDtoMapping(User requester) {
        ItemRequest request = ItemRequest.builder()
                .id(1)
                .user(requester)
                .description("test")
                .created(LocalDateTime.now())
                .build();

        ItemDto itemDto = new ItemDto(1, "item", "desc", true, 1, null);
        ItemRequestDto dto = itemRequestMapper.toDto(request, List.of(itemDto));

        assertAll(
                () -> assertThat(dto.getId(), equalTo(request.getId())),
                () -> assertThat(dto.getUserId(), equalTo(requester.getId())),
                () -> assertThat(dto.getItems(), hasSize(1))
        );
    }

    private void testEdgeCases() {
        assertAll(
                () -> assertThat(itemRequestMapper.toNewModel(null, null), nullValue()),
                () -> assertThat(itemRequestMapper.toDto(null, null), nullValue()),
                () -> assertThat(itemRequestMapper.toDto(
                        ItemRequest.builder().build(), null).getUserId(), nullValue())
        );
    }
}