package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.dto.BookingPeriod;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBookerId(Integer userId);


    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.end < :now")
    boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(@Param("bookerId") Integer bookerId,
                                                           @Param("itemId") Integer itemId,
                                                           @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i WHERE i.owner.id = :ownerId ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerId(@Param("ownerId") Integer ownerId);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingPeriod(b.start, b.end) " +
            "FROM Booking b WHERE b.item.id = :itemId AND b.start > :now ORDER BY b.start")
    Optional<BookingPeriod> findNextBookingByItemId(@Param("itemId") Integer itemId, @Param("now") LocalDateTime now);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingPeriod(b.start, b.end) " +
            "FROM Booking b WHERE b.item.id = :itemId AND b.end < :now ORDER BY b.end DESC")
    Optional<BookingPeriod> findLastBookingByItemId(@Param("itemId") Integer itemId, @Param("now") LocalDateTime now);

    List<Booking> findByItemIdIn(List<Integer> itemIds);
}
