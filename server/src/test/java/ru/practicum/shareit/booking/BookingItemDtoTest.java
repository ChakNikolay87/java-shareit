package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class BookingItemDtoTest {

    @Test
    public void testConstructor() {
        Long expectedItemId = 1L;
        LocalDateTime expectedStartDate = LocalDateTime.of(2025, 3, 9, 10, 0, 0, 0);

        BookingItemDto bookingItemDto = new BookingItemDto(expectedItemId, expectedStartDate);

        assertEquals(expectedItemId, bookingItemDto.getItemId());
        assertEquals(expectedStartDate, bookingItemDto.getStartDate());
    }

    @Test
    public void testItemIdGetterSetter() {
        Long expectedItemId = 2L;
        BookingItemDto bookingItemDto = new BookingItemDto(1L, LocalDateTime.now());
        bookingItemDto.setItemId(expectedItemId);
        assertEquals(expectedItemId, bookingItemDto.getItemId());
    }

    @Test
    public void testStartDateGetterSetter() {
        LocalDateTime expectedStartDate = LocalDateTime.of(2025, 3, 10, 12, 0, 0, 0);
        BookingItemDto bookingItemDto = new BookingItemDto(1L, LocalDateTime.now());
        bookingItemDto.setStartDate(expectedStartDate);
        assertEquals(expectedStartDate, bookingItemDto.getStartDate());
    }
}