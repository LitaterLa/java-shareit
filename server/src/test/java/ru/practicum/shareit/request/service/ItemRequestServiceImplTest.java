package ru.practicum.shareit.request.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.UtilTestDataClass;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {
    private final EntityManager em;
    private final ItemRequestService service;
    private final UserService userService;

    private NewUserRequest newUserRequest;
    private NewItemRequestDto itemRequest1;
    private NewItemRequestDto itemRequest2;
    private UserDto user;
    private int userId;


    @BeforeEach
    void setUp() {
        em.createQuery("DELETE FROM ItemRequest").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();
        newUserRequest = UtilTestDataClass.TestUser.newParis();
        itemRequest1 = UtilTestDataClass.TestItemRequest.newTasteRequest();
        itemRequest2 = UtilTestDataClass.TestItemRequest.newBeautyRequest();

        user = userService.create(newUserRequest);
        userId = user.getId();

    }

    @Test
    void create() {
        service.create(itemRequest1, userId);

        TypedQuery<ItemRequest> query = em.createQuery("select it from ItemRequest it where it.description = :description",
                ItemRequest.class);
        ItemRequest ir = query.setParameter("description", itemRequest1.getDescription()).getSingleResult();

        assertThat(ir.getId(), notNullValue());
        assertThat(ir.getDescription(), equalTo(itemRequest1.getDescription()));
        assertThat(ir.getUser().getId(), equalTo(user.getId()));
        assertThat(ir.getCreated(), notNullValue());
    }

    @Test
    void findUserRequests() {
        ItemRequestDto dto = service.create(itemRequest1, userId);
        List<ItemRequestDto> requests = service.findUserRequests(userId);

        TypedQuery<ItemRequest> query = em.createQuery("select ir from ItemRequest ir where ir.id = :id", ItemRequest.class);
        List<ItemRequest> ir = query.setParameter("id", dto.getId()).getResultList();

        assertThat(requests.size(), equalTo(ir.size()));
        assertThat(requests.getFirst().getId(), equalTo(ir.getFirst().getId()));
        assertThat(requests.getFirst().getUserId(), equalTo(ir.getFirst().getUser().getId()));
        assertThat(requests.getFirst().getCreated(), equalTo(ir.getFirst().getCreated()));
        assertThat(requests.getFirst().getDescription(), equalTo(ir.getFirst().getDescription()));

    }

    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    @Test
    void findAll_NotUserRequests() {
        UserDto user2 = userService.create(new NewUserRequest("User2", "user2@email.com"));

        ItemRequestDto dto1 = service.create(itemRequest1, user2.getId());
        ItemRequestDto dto2 = service.create(itemRequest2, user2.getId());

        List<ItemRequestDto> requests = service.findAll(userId, 0, 2);

        assertThat(requests, hasSize(2));
        assertThat(requests.getFirst().getId(), equalTo(dto2.getId()));
        assertThat(requests.getFirst().getUserId(), equalTo(user2.getId()));
        assertThat(requests.getFirst().getCreated(), equalTo(dto2.getCreated()));
        assertThat(requests.getFirst().getDescription(), equalTo(dto2.getDescription()));

        List<ItemRequestDto> requests2 = service.findAll(userId, 0, 1);
        assertThat(requests2, hasSize(1));

    }

    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    @Test
    void getRequestById() {
        ItemRequestDto dto = service.create(itemRequest1, userId);
        service.getRequestById(dto.getId());

        TypedQuery<ItemRequest> query = em.createQuery("select ir from ItemRequest ir where ir.id = :id", ItemRequest.class);
        ItemRequest ir = query.setParameter("id", dto.getUserId()).getSingleResult();

        assertThat(ir.getId(), equalTo(dto.getId()));
        assertThat(ir.getUser().getId(), equalTo(dto.getUserId()));
        assertThat(ir.getDescription(), equalTo(dto.getDescription()));
        assertThat(ir.getCreated(), equalTo(dto.getCreated()));
    }

    @Test
    void deleteItemRequest() {
        ItemRequestDto dto = service.create(itemRequest1, userId);
        service.deleteItemRequest(dto.getId());

        assertThrows(NotFoundException.class, () -> service.getRequestById(dto.getId()));
    }
}