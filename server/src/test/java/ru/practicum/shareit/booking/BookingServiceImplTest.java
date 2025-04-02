package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.UtilTestDataClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.IllegalAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    private final EntityManager em;
    private final BookingService service;
    private final UserService userService;
    private final ItemService itemService;

    private NewBookingRequest newBooking;
    private NewUserRequest newUserRequest;
    private NewItemRequest newItem;
    private UserDto booker;
    private ItemDto item;
    private int bookerId;


    @BeforeEach
    void setUp() {
        newUserRequest = UtilTestDataClass.TestUser.newParis();
        newBooking = UtilTestDataClass.TestBooking.newBookingRequest();
        newItem = UtilTestDataClass.TestItem.newBeret();
        newItem.setAvailable(true);
        booker = userService.create(newUserRequest);
        bookerId = booker.getId();
        item = itemService.create(newItem, bookerId);
    }

    @Test
    @Transactional
    void createBookingRequest_shouldCreateAndReturnBooking() {
        NewBookingRequest bookingRequest = new NewBookingRequest(
                LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS),
                item.getId()
        );

        BookingDto response = service.createBookingRequest(bookerId, bookingRequest);

        assertThat(response.getId(), notNullValue());
        assertThat(response.getStart(), equalTo(bookingRequest.getStart()));
        assertThat(response.getEnd(), equalTo(bookingRequest.getEnd()));
        assertThat(response.getStatus(), equalTo("WAITING"));

        em.flush();

        Booking dbBooking = em.find(Booking.class, response.getId());
        assertThat(dbBooking, notNullValue());
        assertThat(dbBooking.getBooker().getId(), equalTo(bookerId));
        assertThat(dbBooking.getItem().getId(), equalTo(item.getId()));
    }

    @Test
    void create_FailUserNotFound() {
        newUserRequest.setEmail("em@email.com");
        UserDto user = userService.create(newUserRequest);
        int userId = user.getId();
        ItemDto item = itemService.create(newItem, userId);
        newBooking.setItemId(item.getId());


        UserDto sneaky = UserDto.builder()
                .id(100)
                .name("rnd")
                .email("rnd@email.com")
                .build();

        assertThrows(NotFoundException.class,
                () -> service.createBookingRequest(sneaky.getId(), newBooking));

    }

    @Test
    void createBookingByNotAvailableItemTest() {
        newUserRequest.setEmail("email@email.com");
        UserDto user = userService.create(newUserRequest);
        int userId = user.getId();
        newItem.setAvailable(false);
        ItemDto item = itemService.create(newItem, userId);
        newBooking.setItemId(item.getId());

        assertThrows(BadRequestException.class, () -> service.createBookingRequest(userId, newBooking));
    }

    @Test
    void createBooking_FailStartEqualsEnd() {
        newUserRequest.setEmail("email@email.com");
        UserDto user = userService.create(newUserRequest);
        int userId = user.getId();
        ItemDto item = itemService.create(newItem, userId);
        newBooking.setItemId(item.getId());
        newBooking.setStart(LocalDateTime.of(2025, 03, 30, 20, 00));
        newBooking.setEnd(LocalDateTime.of(2025, 03, 30, 20, 00));

        assertThrows(BadRequestException.class, () -> service.createBookingRequest(userId, newBooking));
    }

    @Test
    void createBooking_EndBeforeStart_OrStartBeforeNow() {
        newUserRequest.setEmail("email@email.com");
        UserDto user = userService.create(newUserRequest);
        int userId = user.getId();
        ItemDto item = itemService.create(newItem, userId);
        newBooking.setItemId(item.getId());
        newBooking.setEnd(LocalDateTime.of(2025, 03, 29, 20, 00));

        assertThrows(BadRequestException.class, () -> service.createBookingRequest(userId, newBooking));

        newBooking.setStart(LocalDateTime.of(2025, 03, 30, 20, 00));
        assertThrows(BadRequestException.class, () -> service.createBookingRequest(userId, newBooking));


    }

    @Test
    void updateBookingStatus() {
        UserDto owner = userService.create(new NewUserRequest("owner", "owner@email.com"));
        ItemDto item = itemService.create(
                new NewItemRequest("Item", "Desc", true, null),
                owner.getId()
        );
        UserDto booker = userService.create(new NewUserRequest("booker", "booker@email.com"));
        BookingDto booking = service.createBookingRequest(
                booker.getId(),
                new NewBookingRequest(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), item.getId())
        );
        BookingDto request = service.updateBookingStatus(booking.getId(), owner.getId(), false);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b where b.id = :id", Booking.class);
        Booking dbbooking = query.setParameter("id", request.getId()).getSingleResult();

        assertThat(dbbooking.getId(), equalTo(request.getId()));
        assertThat(dbbooking.getBooker().getId(), equalTo(request.getBooker().getId()));
        assertThat(dbbooking.getItem().getId(), equalTo(request.getItem().getId()));
        assertThat(dbbooking.getStatus(), equalTo(Status.REJECTED));
        assertThat(dbbooking.getStart(), equalTo(request.getStart()));
        assertThat(dbbooking.getEnd(), equalTo(request.getEnd()));

        em.flush();

        BookingDto newRequest = service.updateBookingStatus(booking.getId(), owner.getId(), true);
        assertThat(dbbooking.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    void approveBookingByNotOwnerItemTest() {
        newUserRequest.setEmail("email@email.com");
        UserDto user = userService.create(newUserRequest);
        int userId = user.getId();
        ItemDto item = itemService.create(newItem, userId);
        int itemId = item.getId();
        newBooking.setItemId(itemId);
        NewUserRequest userDto2 = new NewUserRequest("name", "email@yandex.ru");

        UserDto otherUser = userService.create(userDto2);
        BookingDto savedBooking = service.createBookingRequest(userId, newBooking);
        int bookingId = savedBooking.getId();

        assertThrows(IllegalAccessException.class, () -> service.updateBookingStatus(bookingId, otherUser.getId(), true));
    }

    @Test
    void updateNotExistingUser() {
        assertThrows(NotFoundException.class,
                () -> service.updateBookingStatus(999, 999, true));
    }

    @Test
    void getNotExistingUser() {
        assertThrows(NotFoundException.class,
                () -> service.findById(999, 999));
    }


    @Test
    void findById() {
        UserDto owner = userService.create(new NewUserRequest("owner", "owner@email.com"));
        ItemDto item = itemService.create(
                new NewItemRequest("Item", "Desc", true, null),
                owner.getId()
        );

        UserDto booker = userService.create(new NewUserRequest("booker", "booker@email.com"));
        BookingDto booking = service.createBookingRequest(
                booker.getId(),
                new NewBookingRequest(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), item.getId()));

        BookingDto found = service.findById(booking.getId(), booker.getId());

        TypedQuery<Booking> query = em.createQuery("select b from Booking b where b.id = :id", Booking.class);
        Booking result = query.setParameter("id", booking.getId()).getSingleResult();

        assertThat(found.getId(), notNullValue());
        assertThat(found.getBooker().getId(), equalTo(result.getBooker().getId()));
        assertThat(found.getItem().getId(), equalTo(result.getItem().getId()));
        assertThat(found.getStatus(), equalTo(result.getStatus().toString()));
        assertThat(found.getStart(), equalTo(result.getStart()));
        assertThat(found.getEnd(), equalTo(result.getEnd()));
    }

    @Test
    void findById_FailForOuterUser() {
        UserDto owner = userService.create(new NewUserRequest("owner", "owner@email.com"));
        ItemDto item = itemService.create(
                new NewItemRequest("Item", "Desc", true, null),
                owner.getId()
        );

        BookingDto booking = service.createBookingRequest(
                booker.getId(),
                new NewBookingRequest(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), item.getId()));

        UserDto sneaky = userService.create(new NewUserRequest("owner", "notOwner@email.com"));

        assertThrows(IllegalAccessException.class,
                () -> service.findById(booking.getId(), sneaky.getId()));
    }

    @Test
    void findAllByUserId() {
        UserDto sneaky = UserDto.builder()
                .id(100)
                .name("rnd")
                .email("rnd@email.com")
                .build();

        assertThrows(NotFoundException.class,
                () -> service.findAllByUserId(sneaky.getId(), "WAITING"));

        BookingDto booking1 = service.createBookingRequest(
                bookerId,
                new NewBookingRequest(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item.getId())
        );

        BookingDto booking2 = service.createBookingRequest(
                bookerId,
                new NewBookingRequest(LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), item.getId())
        );

        List<BookingDto> bookings = service.findAllByUserId(bookerId, "ALL");

        TypedQuery<Booking> query = em.createQuery(
                "SELECT b FROM Booking b WHERE b.booker.id = :bookerId ORDER BY b.start DESC",
                Booking.class);
        List<Booking> result = query.setParameter("bookerId", bookerId).getResultList();

        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.size(), equalTo(result.size()));

        assertThat(bookings.get(0).getId(), equalTo(booking2.getId()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(bookerId));
        assertThat(bookings.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(bookings.get(0).getStatus(), equalTo("WAITING"));
    }

    @Test
    void findAllByUser_FailWrongState() {
        BookingDto booking1 = service.createBookingRequest(
                bookerId,
                new NewBookingRequest(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item.getId())
        );

        assertThrows(IllegalArgumentException.class,
                () -> service.findAllByUserId(bookerId, "Wrong"));

    }

    @Test
    void findAllByOwnerId() {
        NewUserRequest owner = UtilTestDataClass.TestUser.newParis();
        owner.setEmail("new@email.com");
        UserDto user = userService.create(owner);
        int ownerId = user.getId();
        ItemDto newItem = itemService.create(this.newItem, ownerId);
        newBooking.setItemId(newItem.getId());
        service.createBookingRequest(bookerId, newBooking);
        List<BookingDto> bookings = service.findAllByOwnerId(ownerId, "WAITING");

        TypedQuery<Booking> query = em.createQuery("select b from Booking b where b.booker.id = :bookerId", Booking.class);
        List<Booking> result = query.setParameter("bookerId", bookerId).getResultList();

        assertThat(bookings.size(), equalTo(result.size()));
        assertThat(bookings.getFirst().getId(), equalTo(result.getFirst().getId()));
        assertThat(bookings.getFirst().getBooker().getId(), equalTo(result.getFirst().getBooker().getId()));
        assertThat(bookings.getFirst().getItem().getId(), equalTo(result.getFirst().getItem().getId()));
        assertThat(bookings.getFirst().getStatus(), equalTo(result.getFirst().getStatus().toString()));
        assertThat(bookings.getFirst().getStart(), equalTo(result.getFirst().getStart()));
        assertThat(bookings.getFirst().getEnd(), equalTo(result.getFirst().getEnd()));

    }

    @Test
    void createBookingByNotExistingUserTest() {
        newUserRequest.setEmail("newerer@email.com");
        UserDto user = userService.create(newUserRequest);
        int userId = user.getId();

        assertThrows(NotFoundException.class, () -> service.createBookingRequest(userId, newBooking));
    }

    @Test
    void createBookingByNotExistingItemTest() {
        newUserRequest.setEmail("newererer@email.com");
        UserDto user = userService.create(newUserRequest);
        int userId = user.getId();

        assertThrows(NotFoundException.class, () -> service.createBookingRequest(userId, newBooking));
    }

    @Test
    void getAllBookingsByUserWrongStateTest() {
        newUserRequest.setEmail("email@email.ru");
        UserDto user = userService.create(newUserRequest);
        int userId = user.getId();

        assertThrows(IllegalArgumentException.class, () -> service.findAllByUserId(userId, "state"));
    }

    @Test
    void findAllByOwnerId_shouldFilterByState() {
        UserDto owner1 = userService.create(new NewUserRequest("owner1", "owner1@email.com"));
        ItemDto item = itemService.create(new NewItemRequest("Item", "Desc", true, null), owner1.getId());

        BookingDto future = service.createBookingRequest(owner1.getId(),
                new NewBookingRequest(LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(5), item.getId()));

        em.flush();

        assertThat(service.findAllByOwnerId(owner1.getId(), "FUTURE"), hasSize(1));

        service.updateBookingStatus(future.getId(), owner1.getId(), false);
        assertThat(service.findAllByOwnerId(owner1.getId(), "REJECTED"), hasSize(1));

    }
}