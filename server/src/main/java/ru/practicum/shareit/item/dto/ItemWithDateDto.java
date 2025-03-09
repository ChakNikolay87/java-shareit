package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemWithDateDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<CommentDto> comments;
}