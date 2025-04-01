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
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.NewCommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCommentBooking;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    private final EntityManager em;
    private final ItemService service;
    private final UserService userService;

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

    @Test
    void createComment() {
        newUserRequest.setEmail("newer@email.com");
        UserDto user = userService.create(newUserRequest);
        int userId = user.getId();
        ItemDto item = service.create(newItem, userId);
        int itemId = item.getId();
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(em.find(Item.class, itemId))
                .booker(em.find(User.class, userId))
                .status(Status.APPROVED)
                .build();
        em.persist(booking);

        service.createComment(newComment, itemId, userId);

        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.text = :text",
                Comment.class);
        Comment comment = query.setParameter("text", newComment.getText())
                .getSingleResult();

        assertThat(comment.getId(), notNullValue());
        assertThat(comment.getAuthor().getId(), equalTo(userId));
        assertThat(comment.getText(), equalTo(newComment.getText()));
    }


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
    void update_FailWrongUser() {
        UserDto sneaky = userService.create(NewUserRequest.builder().name("rnd").email("rnd@email.com").build());
        ItemDto dto = service.create(newItem, userId);
        assertThrows(NotFoundException.class, () -> service.update(dto.getId(), updateItem, sneaky.getId()));
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
    void addCommentWithoutBookingTest() {
        newUserRequest.setEmail("new@email.com");
        UserDto user = userService.create(newUserRequest);
        int userId = user.getId();
        ItemDto item = service.create(newItem, userId);
        int itemId = item.getId();

        assertThrows(Exception.class, () -> service.createComment(newComment, userId, itemId));
    }

    @Test
    void addCommentWithoutItemTest() {
        newUserRequest.setEmail("new@email.com");
        UserDto user = userService.create(newUserRequest);
        ItemDto item = service.create(newItem, userId);
        int userId = user.getId();
        int itemId = item.getId();

        assertThrows(NotFoundException.class, () -> service.createComment(newComment, userId, itemId));
    }

    @Test
    void addCommentWithoutUserTest() {
        int userId = 999;
        ItemDto item = service.create(newItem, userDto.getId());
        int itemId = item.getId();
        assertThrows(NotFoundException.class, () -> service.createComment(newComment, userId, itemId));
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
    void getUserItems_Fail() {
        UserDto sneaky = userService.create(NewUserRequest.builder().name("rnd").email("rnd@email.com").build());
        assertThrows(NotFoundException.class, () -> service.getUserItems(sneaky.getId()));
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
        List<ItemDto> emptyResult = service.search("", 0, 5);
        assertThat(emptyResult, hasSize(0));

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