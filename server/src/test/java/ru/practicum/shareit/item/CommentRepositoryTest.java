package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CommentRepositoryTest {
    @Autowired
    private CommentRepository repository;

    @Autowired
    private TestEntityManager em;

    private User owner, booker;
    private Item savedItem;
    private Booking savedBooking;
    private Comment savedComment;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        owner = em.persist(new User(null, "User1", "user1@email.com"));
        booker = em.persist(new User(null, "User2", "user2@email.com"));
        savedItem = em.persist(new Item(null, "laptop", "powerful laptop", true, owner, null));
        now = LocalDateTime.now();
        savedBooking = em.persist(new Booking(null, now.minusDays(2L), now.minusDays(1L), savedItem, Status.APPROVED, booker));
        savedComment = em.persist(new Comment(null, "good laptop", savedItem, booker, LocalDateTime.now()));

        em.flush();

    }

    @Test
    void findAllByItemId() {
        List<Comment> comments = repository.findAllByItemId(savedItem.getId());

        assertThat(comments, hasSize(1));
        assertThat(comments, hasItem(hasProperty("text", equalTo(savedComment.getText()))));
        assertThat(comments, hasItem(allOf(
                hasProperty("item", notNullValue()),
                hasProperty("author", notNullValue()),
                hasProperty("created", notNullValue()))));

    }

    @Test
    void findAllByItemIds() {
        List<Comment> comments = repository.findAllByItemIds(List.of(savedItem.getId()));

        assertThat(comments, hasSize(1));
        assertThat(comments, hasItem(hasProperty("text", equalTo(savedComment.getText()))));
        assertThat(comments, hasItem(allOf(
                hasProperty("item", notNullValue()),
                hasProperty("author", notNullValue()),
                hasProperty("created", notNullValue()))));

    }
}