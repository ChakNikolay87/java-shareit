package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i " +
            "WHERE i.available = true " +
            "AND (UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            "OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%')))")
    List<Item> search(String text);

    List<Item> findByOwnerId(Long ownerId);

    @Query("SELECT b.item.id, MAX(b.start) FROM Booking b " +
            "WHERE b.item.id IN ?1 AND b.start <= ?2 GROUP BY b.item.id")
    List<Object[]> findLastBookings(List<Long> itemIds, LocalDateTime now);

    @Query("SELECT b.item.id, MIN(b.start) FROM Booking b " +
            "WHERE b.item.id IN ?1 AND b.start > ?2 GROUP BY b.item.id")
    List<Object[]> findNextBookings(List<Long> itemIds, LocalDateTime now);
}