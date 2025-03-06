package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
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

    @Autowired
    private BookingRepository bookingRepository;

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
    public void createBookingValidationTest() throws ItemNotAvailableException {
        item.setAvailable(false);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(ItemNotAvailableException.class, () -> {
            bookingService.createBooking(booker.getId(), bookingDto);
        });

        item.setAvailable(true);
        bookingDto.setItemId(item.getId());

        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(owner.getId(), bookingDto);
        });

        bookingDto.setStart(LocalDateTime.now().minusDays(1));

        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(booker.getId(), bookingDto);
        });

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));

        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(booker.getId(), bookingDto);
        });

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
    public void approveBookingThrowsRuntimeExceptionWhenUserIsNotOwner() throws ItemNotAvailableException {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.createBooking(booker.getId(), bookingDto);

        assertThrows(RuntimeException.class, () -> {
            bookingService.approveBooking(booker.getId(), createdBooking.getId(), true);
        });
    }

    @Test
    public void approveBookingThrowsIllegalStateExceptionWhenBookingAlreadyApproved() throws ItemNotAvailableException, BookingAccessException {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.createBooking(booker.getId(), bookingDto);
        bookingService.approveBooking(owner.getId(), createdBooking.getId(), true);

        assertThrows(IllegalStateException.class, () -> {
            bookingService.approveBooking(owner.getId(), createdBooking.getId(), true);
        });
    }

    @Test
    public void approveBookingThrowsIllegalStateExceptionWhenBookingAlreadyRejected() throws BookingAccessException, ItemNotAvailableException {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.createBooking(booker.getId(), bookingDto);
        bookingService.approveBooking(owner.getId(), createdBooking.getId(), false);

        assertThrows(IllegalStateException.class, () -> {
            bookingService.approveBooking(owner.getId(), createdBooking.getId(), true);
        });
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

        Long otherUserId = 3L;

        assertThrows(BookingAccessException.class, () -> {
            bookingService.getBookingById(otherUserId, createdBooking.getId());
        });
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
    public void getAllBookingsWhenStateIsCurrentShouldReturnBookingsWithinCurrentPeriod() {
        Booking futureBooking = new Booking(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingRepository.save(futureBooking);

        Booking currentBooking = new Booking(null, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), item, booker, BookingStatus.WAITING);
        bookingRepository.save(currentBooking);

        Booking pastBooking = new Booking(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, booker, BookingStatus.WAITING);
        bookingRepository.save(pastBooking);

        List<BookingDto> bookings = bookingService.getAllBookings(booker.getId(), State.CURRENT);

        assertEquals(1, bookings.size());
        assertEquals(currentBooking.getId(), bookings.get(0).getId());
    }

    @Test
    public void getAllBookingsWhenStateIsPastShouldReturnBookingsInPast() {
        Booking pastBooking = new Booking(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, booker, BookingStatus.WAITING);
        bookingRepository.save(pastBooking);

        Booking futureBooking = new Booking(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingRepository.save(futureBooking);

        Booking currentBooking = new Booking(null, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), item, booker, BookingStatus.WAITING);
        bookingRepository.save(currentBooking);

        List<BookingDto> bookings = bookingService.getAllBookings(booker.getId(), State.PAST);

        assertEquals(1, bookings.size());
        assertEquals(pastBooking.getId(), bookings.get(0).getId());
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
    public void getBookingsForOwnerWhenStateIsCurrentShouldReturnBookingsWithinCurrentPeriod() {
        Booking futureBooking = new Booking(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingRepository.save(futureBooking);

        Booking currentBooking = new Booking(null, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), item, booker, BookingStatus.WAITING);
        bookingRepository.save(currentBooking);

        Booking pastBooking = new Booking(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, booker, BookingStatus.WAITING);
        bookingRepository.save(pastBooking);

        List<BookingDto> bookings = bookingService.getBookingsForOwner(item.getOwner().getId(), State.CURRENT);

        assertEquals(1, bookings.size());
        assertEquals(currentBooking.getId(), bookings.get(0).getId());
    }

    @Test
    public void getBookingsForOwnerWhenStateIsPastShouldReturnBookingsInPast() {
        Booking pastBooking = new Booking(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, booker, BookingStatus.WAITING);
        bookingRepository.save(pastBooking);

        Booking futureBooking = new Booking(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingRepository.save(futureBooking);

        Booking currentBooking = new Booking(null, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), item, booker, BookingStatus.WAITING);
        bookingRepository.save(currentBooking);

        List<BookingDto> bookings = bookingService.getBookingsForOwner(item.getOwner().getId(), State.PAST);

        assertEquals(1, bookings.size());
        assertEquals(pastBooking.getId(), bookings.get(0).getId());
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