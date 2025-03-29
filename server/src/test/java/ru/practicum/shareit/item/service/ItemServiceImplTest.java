package ru.practicum.shareit.item.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.UtilTestDataClass;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.NewCommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCommentBooking;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    private final EntityManager em;
    private final ItemService service;
    private final UserService userService;
    private final BookingService bookingService;

    private NewUserRequest newUserRequest;
    private NewItemRequest newItem;
    private NewItemRequest newUnavailableItem;
    private UpdateItem updateItem;
    private UserDto userDto;
    private NewCommentRequest newComment;
    private int userId;


    @BeforeEach
    void setUp() {
        newUserRequest = UtilTestDataClass.TestUser.newParis();
        newItem = UtilTestDataClass.TestItem.newScarf();
        newUnavailableItem = UtilTestDataClass.TestItem.newBeret();
        updateItem = UtilTestDataClass.TestItem.updateBeret();
        userDto = userService.create(newUserRequest);
        newComment = UtilTestDataClass.TestComment.newScarfComment();
        userId = userDto.getId();
    }

    @Test
    void create() {
        service.create(newItem, userDto.getId());

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", newItem.getName()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(newItem.getName()));
        assertThat(item.getDescription(), equalTo(newItem.getDescription()));
        assertThat(item.getAvailable(), equalTo(newItem.getAvailable()));
        assertThat(item.getRequestId(), equalTo(newItem.getRequestId()));

    }

    @Test
    void createComment_beforeBookingIsOver() {
        ItemDto dto = service.create(newItem, userDto.getId());
        assertThrows(BadRequestException.class, () -> {
            service.createComment(newComment, dto.getId(), userId);
        });
    }

//    @Test
//    void createComment_shouldFailWhenBookingNotFinished() throws InterruptedException {
//        ItemDto dto = service.create(newItem, userId);
//        bookingService.createBookingRequest(userId,
//                new NewBookingRequest(
//                        LocalDateTime.now().plusNanos(1000000000),
//                        LocalDateTime.now().plusNanos(2000000000),
//                        dto.getId()
//                )
//        );
//        Thread.sleep(5000);
//        NewCommentRequest commentRequest = UtilTestDataClass.TestComment.newScarfComment();
//        CommentDto comment = service.createComment(commentRequest, dto.getId(), userId);
//
//        assertThat(comment.getId(), notNullValue());
//        assertThat(commentRequest.getText(), equalTo(comment.getText()));
//    }

//    @Test
//    void createCommentShouldFailWhenBookingNotFinished() {
//        ItemDto dto = service.create(newItem, userId);
//
//        // 1. Фиксируем даты в прошлом и будущем
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime pastStart = now.minusDays(2);
//        LocalDateTime pastEnd = now.minusDays(1);
//        LocalDateTime futureStart = now.plusDays(1);
//        LocalDateTime futureEnd = now.plusDays(2);
//
//
//        // 3. Тест 1: Комментирование после завершенного бронирования (должно работать)
//        bookingService.createBookingRequest(userId,
//                new NewBookingRequest(
//                        LocalDateTime.now().plusNanos(1000000000),
//                        LocalDateTime.now().plusNanos(2000000000),
//                        dto.getId()
//                )
//        );
//
//        NewCommentRequest commentRequest = UtilTestDataClass.TestComment.newScarfComment();
//        CommentDto comment = service.createComment(commentRequest, dto.getId(), userId);
//
//        assertThat(comment.getId(), notNullValue());
//        assertThat(commentRequest.getText(), equalTo(comment.getText()));
//
//        // 4. Тест 2: Комментирование активного бронирования (должно падать)
//        BookingDto activeBooking = bookingService.createBookingRequest(userId,
//                new NewBookingRequest(pastEnd, futureStart, dto.getId()) // еще активно
//        );
//
//        assertThrows(BadRequestException.class, () -> {
//            service.createComment(commentRequest, dto.getId(), userId);
//        });
//
//        // 5. Тест 3: Комментирование до начала бронирования (должно падать)
//        BookingDto futureBooking = bookingService.createBookingRequest( userId,
//                new NewBookingRequest(futureStart, futureEnd, dto.getId())
//        );
//
//        assertThrows(BadRequestException.class, () -> {
//            service.createComment(commentRequest, dto.getId(),userId);
//        });
//    }

    @Test
    void update() {
        ItemDto dto = service.create(newItem, userId);
        service.update(dto.getId(), updateItem, userId);

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", updateItem.getName()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(updateItem.getName()));
        assertThat(item.getDescription(), equalTo(updateItem.getDescription()));
        assertThat(item.getAvailable(), equalTo(updateItem.getAvailable()));

    }

    @Test
    void get() {
        ItemDto dto = service.create(newItem, userId);
        service.get(dto.getId(), userId);

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", dto.getId()).getSingleResult();

        assertThat(item.getId(), equalTo(dto.getId()));
        assertThat(item.getName(), equalTo(dto.getName()));
        assertThat(item.getDescription(), equalTo(dto.getDescription()));
        assertThat(item.getAvailable(), equalTo(dto.getAvailable()));
        assertThat(item.getRequestId(), equalTo(dto.getRequestId()));
        assertThat(item.getOwner().getId(), equalTo(dto.getOwnerId()));

    }

    @Test
    void getUserItems() {
        ItemDto dto = service.create(newItem, userId);
        List<ItemDtoCommentBooking> itemsAll = service.getUserItems(userId);

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.owner.id = :ownerId", Item.class);
        List<Item> items = query.setParameter("ownerId", dto.getOwnerId()).getResultList();

        assertThat(items.getFirst().getId(), equalTo(itemsAll.getFirst().getId()));
        assertThat(items.getFirst().getName(), equalTo(itemsAll.getFirst().getName()));
        assertThat(items.getFirst().getDescription(), equalTo(itemsAll.getFirst().getDescription()));
        assertThat(items.getFirst().getAvailable(), equalTo(itemsAll.getFirst().getAvailable()));
        assertThat(items.getFirst().getRequestId(), equalTo(itemsAll.getFirst().getRequestId()));
        assertThat(items.getFirst().getOwner().getId(), equalTo(itemsAll.getFirst().getOwnerId()));
    }

    @Test
    void updateNotExistingUser() {
        assertThrows(NotFoundException.class,
                () -> service.update(999, updateItem, userId));
    }

    @Test
    void getNotExistingUser() {
        assertThrows(NotFoundException.class,
                () -> service.get(999, userId));
    }

    @Test
    void searchUnavailable() {
        ItemDto dto = service.create(newUnavailableItem, userId);
        List<ItemDto> result = service.search("beret", 0, 5);

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.name LIKE :name", Item.class);
        Item i = query.setParameter("name", dto.getName()).getSingleResult();

        assertThat(i.getId(), notNullValue());
        assertThat(result, empty());

    }

    @Test
    void searchAvailable() {
        ItemDto dto = service.create(newItem, userId);
        List<ItemDto> result = service.search("scarf", 0, 5);

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.name LIKE :name", Item.class);
        Item i = query.setParameter("name", dto.getName()).getSingleResult();

        assertThat(i.getId(), equalTo(result.get(0).getId()));
        assertThat(i.getName(), equalTo(result.getFirst().getName()));
        assertThat(i.getDescription(), equalTo(result.getFirst().getDescription()));
        assertThat(i.getAvailable(), equalTo(result.getFirst().getAvailable()));
        assertThat(i.getRequestId(), equalTo(result.getFirst().getRequestId()));
        assertThat(i.getOwner().getId(), equalTo(result.getFirst().getOwnerId()));
    }

    @Test
    void delete() {
        ItemDto dto = service.create(newItem, userId);
        service.delete(dto.getId());

        assertThrows(NotFoundException.class, () -> service.get(dto.getId(), userId));
    }
}