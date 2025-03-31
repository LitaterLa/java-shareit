package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository repository;

    @Autowired
    private TestEntityManager em;

    private User user1, user2;
    private ItemRequest request1, request2, request3;

    @BeforeEach
    void setUp() {
        user1 = em.merge(new User(null, "User1", "user1@email.com"));
        user2 = em.merge(new User(null, "User2", "user2@email.com"));

        request1 = em.merge(new ItemRequest(1, "Нужна дрель", LocalDateTime.now(), user1));
        request2 = em.merge(new ItemRequest(2, "Нужен молоток", LocalDateTime.now(), user2));
        request3 = em.merge(new ItemRequest(3, "Нужна отвертка", LocalDateTime.now(), user1));
    }

    @Test
    void findByUserIdNot_shouldReturnOthersRequestsWithPagination() {
        List<ItemRequest> result = repository.findByUserIdNot(
                user1.getId(),
                PageRequest.of(0, 1)
        );

        assertThat(result, hasSize(1));
        assertThat("Нужен молоток", equalTo(result.get(0).getDescription()));

        List<ItemRequest> resultPage2 = repository.findByUserIdNot(
                user1.getId(),
                PageRequest.of(1, 1)
        );

        assertThat(resultPage2, empty());
    }
}