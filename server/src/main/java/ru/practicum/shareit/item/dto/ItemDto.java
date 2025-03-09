package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private List<CommentDto> comments;
}