package ru.practicum.shareit.booking;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class BookingMapperTest {

    private Booking booking;
    private BookingDto bookingDto;
    private Item item;
    private User booker;

    @BeforeEach
    public void setUp() {
        booker = new User(1L, "Booker", "booker@example.com");
        item = new Item(1L, "Item 1", "Item Description", true, booker, null,
                new ArrayList<>());

        booking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), item, booker,
                BookingStatus.WAITING);
        bookingDto = new BookingDto(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
                ItemMapper.toItemDto(item), UserMapper.toUserDto(booker), BookingStatus.WAITING, item.getId(),
                booker.getId());
    }

    @Test
    public void toBookingDtoTest() {
        BookingDto result = BookingMapper.toBookingDto(booking);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(ItemMapper.toItemDto(item), result.getItem());
        assertEquals(UserMapper.toUserDto(booker), result.getBooker());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(item.getId(), result.getItemId());
        assertEquals(booker.getId(), result.getBookerId());
    }

    @Test
    public void toBookingTest() {
        Booking result = BookingMapper.toBooking(bookingDto, item, booker);

        assertNotNull(result);
        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(bookingDto.getStart(), result.getStart());
        assertEquals(bookingDto.getEnd(), result.getEnd());
        assertEquals(item, result.getItem());
        assertEquals(booker, result.getBooker());
        assertEquals(bookingDto.getStatus(), result.getStatus());
    }
}