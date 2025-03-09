package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingItemDto {
    private Long itemId;
    private LocalDateTime startDate;
}
