package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_Id(Long bookerId);

    List<Booking> findByItem_Owner_Id(Long ownerId);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime start);

    List<Booking> findByBooker_IdAndStatus(Long bookerId, BookingStatus status);

    List<Booking> findByItem_Owner_IdAndEndIsBefore(Long ownerId, LocalDateTime end);

    List<Booking> findByItem_Owner_IdAndStartIsAfter(Long ownerId, LocalDateTime start);

    List<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItem_Owner_IdAndStatus(Long ownerId, BookingStatus status);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBooker_IdAndItem_IdAndEndIsBefore(Long bookerId, Long itemId, LocalDateTime end);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.end <= :now ORDER BY b.start DESC")
    List<Booking> findLastBooking(Long itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.start > :now ORDER BY b.start ASC")
    List<Booking> findNextBooking(Long itemId, LocalDateTime now);

    boolean existsByBooker_IdAndItem_IdAndEndIsBefore(Long userId, Long itemId, LocalDateTime now);
}