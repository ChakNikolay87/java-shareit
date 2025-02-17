package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ItemNotAvailableException;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingDto bookingDto) throws ItemNotAvailableException;

    BookingDto approveBooking(Long ownerId, Long bookingId, boolean approved);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getAllBookings(Long userId, String state);

    List<BookingDto> getBookingsForOwner(Long ownerId, String state);
}