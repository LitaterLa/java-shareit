package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingPeriod;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingRepositoryTest {
    @Autowired
    private BookingRepository repository;

    @Autowired
    private TestEntityManager em;

    private User owner, booker;
    private Item savedItem;
    private Booking savedBooking;


    @BeforeEach
    void setUp() {
        owner = em.persist(new User(null, "User1", "user1@email.com"));
        booker = em.persist(new User(null, "User2", "user2@email.com"));
        savedItem = em.persist(new Item(null, "laptop", "powerful laptop", true, owner, null));
        savedBooking = em.persist(new Booking(null, LocalDateTime.now().plusDays(1L), LocalDateTime.now().plusDays(2L), savedItem, Status.APPROVED, booker));

        em.flush();
    }

    @Test
    void findAllByBookerId() {
        List<Booking> bookings = repository.findAllByBookerId(booker.getId());

        assertThat(1, equalTo(bookings.size()));
        assertThat(bookings.get(0).getStart(), notNullValue());
        assertThat(bookings.get(0).getEnd(), notNullValue());

    }

    @Test
    void existsByBookerIdAndItemIdAndStatusAndEndBefore() {
        LocalDateTime now = LocalDateTime.now();
        boolean isFalse = repository.existsByBookerIdAndItemIdAndStatusAndEndBefore(booker.getId(), savedItem.getId(), now);
        assertThat(isFalse, equalTo(false));

        Booking rejectedBooking = em.merge(new Booking(null, now.minusDays(2L), now.minusDays(1L), savedItem, Status.REJECTED, booker));
        boolean isRejected = repository.existsByBookerIdAndItemIdAndStatusAndEndBefore(booker.getId(), savedItem.getId(), now);
        assertThat(isRejected, equalTo(false));

        em.flush();

        Booking pastBooking = em.merge(new Booking(null, now.minusDays(2L), now.minusDays(1L), savedItem, Status.APPROVED, booker));
        boolean isTrue = repository.existsByBookerIdAndItemIdAndStatusAndEndBefore(booker.getId(), savedItem.getId(), now);
        assertThat(isTrue, equalTo(true));

    }

    @Test
    void findAllByItemOwnerId() {
        List<Booking> bookings = repository.findAllByItemOwnerId(owner.getId());

        assertThat(1, equalTo(bookings.size()));
        assertThat(bookings.get(0).getStart(), notNullValue());
        assertThat(bookings.get(0).getEnd(), notNullValue());
    }

    @Test
    void findNextBookingByItemId() {
        LocalDateTime now = LocalDateTime.now();
        Optional<BookingPeriod> next = repository.findNextBookingByItemId(savedItem.getId(), now);

        assertThat(next, notNullValue());
        assertThat(savedBooking.getStart(), equalTo(next.get().getStart()));
    }

    @Test
    void findLastBookingByItemId() {
        LocalDateTime now = LocalDateTime.now();
        Booking pastBooking = em.merge(new Booking(null, now.minusDays(2L), now.minusDays(1L), savedItem, Status.APPROVED, booker));
        Optional<BookingPeriod> past = repository.findLastBookingByItemId(savedItem.getId(), now);

        assertThat(past, notNullValue());
        assertThat(pastBooking.getStart(), equalTo(past.get().getStart()));
    }

}