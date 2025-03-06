package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exception.BookingAccessException;
import ru.practicum.shareit.exception.ItemNotAvailableException;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingDto bookingDto) throws ItemNotAvailableException;

    BookingDto approveBooking(Long ownerId, Long bookingId, boolean approved) throws BookingAccessException;

    BookingDto getBookingById(Long userId, Long bookingId) throws BookingAccessException;

    List<BookingDto> getAllBookings(Long userId, State state);

    List<BookingDto> getBookingsForOwner(Long ownerId, State state);
}