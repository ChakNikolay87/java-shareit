package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class BookingServiceIntegrationTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingService bookingService;

    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setName("Test User");
        booker.setEmail("test@example.com");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(booker);
        item = itemRepository.save(item);
    }

    @Test
    void createBookingShouldCreateBookingWhenValidData() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);

        assertNotNull(savedBooking);
        assertNotNull(savedBooking.getId());
        assertEquals(item.getId(), savedBooking.getItem().getId());
        assertEquals(booker.getId(), savedBooking.getBooker().getId());
        assertEquals(bookingDto.getStart(), savedBooking.getStart());
        assertEquals(bookingDto.getEnd(), savedBooking.getEnd());
        assertEquals(BookingStatus.WAITING, savedBooking.getStatus());
    }

    @Test
    void createBookingShouldThrowExceptionWhenItemNotAvailable() {
        item.setAvailable(false);
        itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));

        assertThrows(ItemNotAvailableException.class, () -> {
            bookingService.createBooking(booker.getId(), bookingDto);
        });
    }

    @Test
    void createBookingShouldThrowExceptionWhenBookingDatesAreInvalid() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));

        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(booker.getId(), bookingDto);
        });
    }

    @Test
    void createBookingShouldThrowExceptionWhenBookerIsOwner() {
        item.setOwner(booker);
        itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));

        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(booker.getId(), bookingDto);
        });
    }
}
