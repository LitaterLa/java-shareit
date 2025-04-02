package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryTest {
    @Autowired
    private ItemRepository repository;

    @Autowired
    private TestEntityManager em;

    private User savedUser;
    private User requestor;
    private Item savedItem1;
    private Item savedItem2;
    private Item savedItemWithRequest;
    private ItemRequest savedRequest1;
    private ItemRequest savedRequest2;


    @BeforeEach
    void setUp() {
        User user = User.builder()
                .name("Phoebe")
                .email("phoebe@email.com")
                .build();
        savedUser = em.persist(user);

        requestor = em.persist(User.builder()
                .name("Ross")
                .email("ross@email.com")
                .build());

        ItemRequest request1 = ItemRequest.builder()
                .description("Need some item")
                .user(requestor)
                .created(LocalDateTime.now())
                .build();
        savedRequest1 = em.persist(request1);

        ItemRequest request2 = ItemRequest.builder()
                .description("Need another item")
                .user(requestor)
                .created(LocalDateTime.now())
                .build();
        savedRequest2 = em.persist(request2);

        savedItem1 = em.persist(Item.builder()
                .name("phone")
                .description("smartphone with camera")
                .available(true)
                .owner(savedUser)
                .build());

        savedItem2 = em.persist(Item.builder()
                .name("laptop")
                .description("powerful laptop for work")
                .available(true)
                .owner(savedUser)
                .build());

        savedItemWithRequest = em.persist(Item.builder()
                .name("monitor")
                .description("4K monitor")
                .available(true)
                .owner(savedUser)
                .requestId(savedRequest1.getId())
                .build());

        em.flush();

    }


    @Test
    void findAllByOwnerId() {
        List<Item> items = repository.findAllByOwnerId(savedUser.getId());
        assertThat(items, hasSize(3));
        assertThat(items, hasItem(hasProperty("id", equalTo(savedItem1.getId()))));
        assertThat(items, hasItems(hasProperty("id", equalTo(savedItem2.getId()))));
        assertThat(items, hasItem(hasProperty("id", equalTo(savedItemWithRequest.getId()))));

    }

    @Test
    void findAllByRequestId() {
        List<Item> items = repository.findAllByRequestId(savedRequest1.getId());
        assertThat(items, hasSize(1));
        assertThat(items, hasItem(hasProperty("id", equalTo(savedItemWithRequest.getId()))));
    }

    @Test
    void findAllByRequestIds() {
        List<Integer> requests = List.of(savedRequest1.getId(), savedRequest2.getId());
        List<Item> items = repository.findAllByRequestIds(requests);
        assertThat(items, hasSize(1));
        assertThat(items, hasItem(hasProperty("id", equalTo(savedItemWithRequest.getId()))));
    }

    @Test
    void findAllByNameOrDescriptionContaining() {
        List<Item> items = repository.findAllByNameOrDescriptionContaining(
                "laptop",
                PageRequest.of(0, 1));

        assertThat(items, hasSize(1));
        assertThat(items, hasItem(hasProperty("id", equalTo(savedItem2.getId()))));

        List<Item> items2 = repository.findAllByNameOrDescriptionContaining(
                "laptop",
                PageRequest.of(1, 1));

        assertThat(items2, empty());
    }
}