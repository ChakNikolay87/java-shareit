package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BookingAccessException;
import ru.practicum.shareit.exception.ItemNotAvailableException;

import java.util.List;

import static ru.practicum.shareit.util.HeaderConstants.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                    @RequestBody BookingDto bookingDto) throws ItemNotAvailableException {
        return new ResponseEntity<>(bookingService.createBooking(userId, bookingDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                                                     @PathVariable Long bookingId,
                                                     @RequestParam boolean approved) throws BookingAccessException {
        return new ResponseEntity<>(bookingService.approveBooking(ownerId, bookingId, approved), HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                     @PathVariable Long bookingId) throws BookingAccessException {
        return new ResponseEntity<>(bookingService.getBookingById(userId, bookingId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllBookings(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                           @RequestParam(defaultValue = "ALL") State state) {
        return new ResponseEntity<>(bookingService.getAllBookings(userId, state), HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getBookingsForOwner(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                                                                @RequestParam(defaultValue = "ALL") State state) {
        return new ResponseEntity<>(bookingService.getBookingsForOwner(ownerId, state), HttpStatus.OK);
    }
}