package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingAccessException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.BookingStatus.*;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public BookingDto createBooking(Long userId, BookingDto bookingDto) throws ItemNotAvailableException {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с id=" + userId + " не найден"));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NoSuchElementException("Вещь с id=" + bookingDto.getItemId() + " не найдена"));

        if (!item.getAvailable()) {
            throw new ItemNotAvailableException("Вещь с id=" + item.getId() + " недоступна для бронирования");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Владелец вещи не может бронировать свою же вещь");
        }

        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Дата начала бронирования не может быть в прошлом");
        }

        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            throw new IllegalArgumentException("Дата окончания бронирования должна быть позже даты начала");
        }

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Бронирование с id=" + bookingId + " не найдено"));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Только владелец вещи с id=" + booking.getItem().getId() + " может подтвердить или отклонить бронирование");
        }

        if (booking.getStatus() == APPROVED) {
            throw new IllegalStateException("Невозможно изменить статус: бронирование уже подтверждено.");
        }

        if (booking.getStatus() == REJECTED) {
            throw new IllegalStateException("Невозможно одобрить отклоненное бронирование. Создайте новый запрос на бронирование.");
        }

        booking.setStatus(approved ? APPROVED : REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(updatedBooking);
    }


    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) throws BookingAccessException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Бронирование не найдено"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new BookingAccessException("Пользователь не имеет доступа к бронированию");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookings(Long userId, State state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
        List<Booking> bookings = switch (state) {
            case CURRENT -> bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                    LocalDateTime.now());
            case PAST -> bookingRepository.findByBooker_IdAndEndIsBefore(userId, LocalDateTime.now());
            case FUTURE -> bookingRepository.findByBooker_IdAndStartIsAfter(userId, LocalDateTime.now());
            case WAITING -> bookingRepository.findByBooker_IdAndStatus(userId, WAITING);
            case REJECTED -> bookingRepository.findByBooker_IdAndStatus(userId, REJECTED);
            default -> bookingRepository.findByBooker_Id(userId);
        };
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsForOwner(Long ownerId, State state) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
        List<Booking> bookings = switch (state) {
            case CURRENT -> bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(ownerId,
                    LocalDateTime.now(), LocalDateTime.now());
            case PAST -> bookingRepository.findByItem_Owner_IdAndEndIsBefore(ownerId, LocalDateTime.now());
            case FUTURE -> bookingRepository.findByItem_Owner_IdAndStartIsAfter(ownerId, LocalDateTime.now());
            case WAITING -> bookingRepository.findByItem_Owner_IdAndStatus(ownerId, WAITING);
            case REJECTED -> bookingRepository.findByItem_Owner_IdAndStatus(ownerId, REJECTED);
            default -> bookingRepository.findByItem_Owner_Id(ownerId);
        };
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
