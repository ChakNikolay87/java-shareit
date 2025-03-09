package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testBookingDtoSerialization() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "ItemName", "ItemDescription", true, null, null);
        UserDto userDto = new UserDto(1L, "UserName", "user@example.com");
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), itemDto, userDto, BookingStatus.APPROVED, 1L, 1L);

        String json = objectMapper.writeValueAsString(bookingDto);

        assertThat(json).contains(
                "\"id\":1",
                "\"start\"",
                "\"end\"",
                "\"item\":",
                "\"booker\":",
                "\"status\":\"APPROVED\"",
                "\"itemId\":1",
                "\"bookerId\":1"
        );
    }


    @Test
    public void testBookingDtoDeserialization() throws Exception {
        String json = "{\"id\":1,\"start\":\"2025-03-03T12:00:00\",\"end\":\"2025-03-04T12:00:00\",\"item\":{\"id\":1,\"name\":\"ItemName\",\"description\":\"ItemDescription\",\"available\":true},\"booker\":{\"id\":1,\"name\":\"UserName\",\"email\":\"user@example.com\"},\"status\":\"APPROVED\",\"itemId\":1,\"bookerId\":1}";

        BookingDto bookingDto = objectMapper.readValue(json, BookingDto.class);

        assertThat(bookingDto.getId()).isEqualTo(1L);
        assertThat(bookingDto.getStart()).isEqualTo(LocalDateTime.parse("2025-03-03T12:00:00"));
        assertThat(bookingDto.getEnd()).isEqualTo(LocalDateTime.parse("2025-03-04T12:00:00"));
        assertThat(bookingDto.getItem().getId()).isEqualTo(1L);
        assertThat(bookingDto.getBooker().getId()).isEqualTo(1L);
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(bookingDto.getItemId()).isEqualTo(1L);
        assertThat(bookingDto.getBookerId()).isEqualTo(1L);
    }

    @Test
    void testEqualsAndHashCode() {
        BookingDto booking1 = new BookingDto();
        booking1.setId(1L);
        booking1.setStart(LocalDateTime.of(2025, 3, 5, 12, 0));
        booking1.setEnd(LocalDateTime.of(2025, 3, 6, 12, 0));
        booking1.setBooker(new UserDto(1L, "User1", "user1@example.com"));
        booking1.setStatus(BookingStatus.APPROVED);
        booking1.setItemId(1L);
        booking1.setBookerId(1L);

        BookingDto booking2 = new BookingDto();
        booking2.setId(1L);
        booking2.setStart(LocalDateTime.of(2025, 3, 5, 12, 0));
        booking2.setEnd(LocalDateTime.of(2025, 3, 6, 12, 0));
        booking2.setBooker(new UserDto(1L, "User1", "user1@example.com"));
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setItemId(1L);
        booking2.setBookerId(1L);

        assertEquals(booking1, booking2);

        assertEquals(booking1.hashCode(), booking2.hashCode());

        BookingDto booking3 = new BookingDto();
        booking3.setId(2L);  // Разный ID
        booking3.setStart(LocalDateTime.of(2025, 3, 5, 12, 0));
        booking3.setEnd(LocalDateTime.of(2025, 3, 6, 12, 0));
        booking3.setBooker(new UserDto(2L, "User2", "user2@example.com"));  // Разный User
        booking3.setStatus(BookingStatus.REJECTED);  // Разный статус
        booking3.setItemId(2L);
        booking3.setBookerId(2L);

        assertNotEquals(booking1, booking3);
        assertNotEquals(booking2, booking3);

        assertNotEquals(booking1.hashCode(), booking3.hashCode());

        assertNotEquals(booking1, null);
        assertNotEquals(booking1, new Object());
    }

    @Test
    public void testEqualsSameObject() {
        ItemDto itemDto = new ItemDto(1L, "ItemName", "ItemDescription", true, null, null);
        UserDto userDto = new UserDto(1L, "UserName", "user@example.com");
        BookingDto bookingDto1 = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), itemDto, userDto, BookingStatus.APPROVED, 1L, 1L);
        BookingDto bookingDto2 = bookingDto1;

        assertTrue(bookingDto1.equals(bookingDto2));
    }


    @Test
    public void testEqualsNullObject() {
        ItemDto itemDto = new ItemDto(1L, "ItemName", "ItemDescription", true, null, null);
        UserDto userDto = new UserDto(1L, "UserName", "user@example.com");
        BookingDto bookingDto1 = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), itemDto, userDto, BookingStatus.APPROVED, 1L, 1L);

        assertFalse(bookingDto1.equals(null));
    }

    @Test
    public void testEqualsDifferentClass() {
        ItemDto itemDto = new ItemDto(1L, "ItemName", "ItemDescription", true, null, null);
        UserDto userDto = new UserDto(1L, "UserName", "user@example.com");
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), itemDto, userDto, BookingStatus.APPROVED, 1L, 1L);

        assertFalse(bookingDto.equals("SomeString"));
    }
}
