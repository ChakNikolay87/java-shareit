package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BookingAccessException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User booker;
    private User owner;
    private Item item;

    @BeforeEach
    public void setUp() {
        booker = new User(null, "Booker", "booker@example.com");
        owner = new User(null, "Owner", "owner@example.com");
        userRepository.save(booker);
        userRepository.save(owner);

        item = new Item(null, "Item 1", "Description", true, owner, null, new ArrayList<>());
        itemRepository.save(item);
    }

    @Test
    public void createBookingTest() throws ItemNotAvailableException {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.createBooking(booker.getId(), bookingDto);

        assertNotNull(createdBooking);
        assertEquals(bookingDto.getItemId(), createdBooking.getItemId());
        assertEquals(BookingStatus.WAITING, createdBooking.getStatus());
        assertNotNull(createdBooking.getId());
    }

    @Test
    public void approveBookingTest() throws BookingAccessException, ItemNotAvailableException {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.createBooking(booker.getId(), bookingDto);
        BookingDto approvedBooking = bookingService.approveBooking(owner.getId(), createdBooking.getId(), true);

        assertNotNull(approvedBooking);
        assertEquals(BookingStatus.APPROVED, approvedBooking.getStatus());
    }

    @Test
    public void getBookingByIdTest() throws ItemNotAvailableException, BookingAccessException {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.createBooking(booker.getId(), bookingDto);
        BookingDto fetchedBooking = bookingService.getBookingById(booker.getId(), createdBooking.getId());

        assertNotNull(fetchedBooking);
        assertEquals(createdBooking.getId(), fetchedBooking.getId());
    }

    @Test
    public void getAllBookingsTest() throws ItemNotAvailableException {
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setItemId(item.getId());
        bookingDto1.setStart(LocalDateTime.now().plusDays(1));
        bookingDto1.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.createBooking(booker.getId(), bookingDto1);

        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setItemId(item.getId());
        bookingDto2.setStart(LocalDateTime.now().plusDays(3));
        bookingDto2.setEnd(LocalDateTime.now().plusDays(4));
        bookingService.createBooking(booker.getId(), bookingDto2);

        List<BookingDto> bookings = bookingService.getAllBookings(booker.getId(), State.ALL);

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
    }

    @Test
    public void getBookingsForOwnerTest() throws ItemNotAvailableException {
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setItemId(item.getId());
        bookingDto1.setStart(LocalDateTime.now().plusDays(1));
        bookingDto1.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.createBooking(booker.getId(), bookingDto1);

        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setItemId(item.getId());
        bookingDto2.setStart(LocalDateTime.now().plusDays(3));
        bookingDto2.setEnd(LocalDateTime.now().plusDays(4));
        bookingService.createBooking(booker.getId(), bookingDto2);

        List<BookingDto> bookings = bookingService.getBookingsForOwner(owner.getId(), State.ALL);

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
    }

    @Test
    public void getAllBookingsFutureStateTest() throws ItemNotAvailableException {
        BookingDto bookingDto = createBookingForUser(booker, item, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        List<BookingDto> bookings = bookingService.getAllBookings(booker.getId(), State.FUTURE);

        assertNotNull(bookings);
        assertTrue(bookings.size() > 0);
    }

    @Test
    public void getAllBookingsWaitingStateTest() throws ItemNotAvailableException {
        BookingDto bookingDto = createBookingForUser(booker, item, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
        bookingService.createBooking(booker.getId(), bookingDto);

        List<BookingDto> bookings = bookingService.getAllBookings(booker.getId(), State.WAITING);

        assertNotNull(bookings);
        assertTrue(bookings.size() > 0);
    }

    @Test
    public void getAllBookingsRejectedStateTest() throws ItemNotAvailableException, BookingAccessException {
        BookingDto bookingDto = createBookingForUser(booker, item, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
        bookingService.createBooking(booker.getId(), bookingDto);
        bookingService.approveBooking(owner.getId(), bookingDto.getId(), false);

        List<BookingDto> bookings = bookingService.getAllBookings(booker.getId(), State.REJECTED);

        assertNotNull(bookings);
        assertTrue(bookings.size() > 0);
    }


    @Test
    public void getBookingsForOwnerFutureStateTest() throws ItemNotAvailableException {
        BookingDto bookingDto = createBookingForUser(booker, item, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        List<BookingDto> bookings = bookingService.getBookingsForOwner(owner.getId(), State.FUTURE);

        assertNotNull(bookings);
        assertTrue(bookings.size() > 0);
    }

    @Test
    public void getBookingsForOwnerWaitingStateTest() throws ItemNotAvailableException {
        BookingDto bookingDto = createBookingForUser(booker, item, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
        bookingService.createBooking(booker.getId(), bookingDto);

        List<BookingDto> bookings = bookingService.getBookingsForOwner(owner.getId(), State.WAITING);

        assertNotNull(bookings);
        assertTrue(bookings.size() > 0);
    }

    @Test
    public void getBookingsForOwnerRejectedStateTest() throws ItemNotAvailableException, BookingAccessException {
        BookingDto bookingDto = createBookingForUser(booker, item, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
        bookingService.createBooking(booker.getId(), bookingDto);
        bookingService.approveBooking(owner.getId(), bookingDto.getId(), false);

        List<BookingDto> bookings = bookingService.getBookingsForOwner(owner.getId(), State.REJECTED);

        assertNotNull(bookings);
        assertTrue(bookings.size() > 0);
    }


    private BookingDto createBookingForUser(User user, Item item, LocalDateTime start, LocalDateTime end)
            throws ItemNotAvailableException {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        return bookingService.createBooking(user.getId(), bookingDto);
    }
}