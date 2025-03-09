package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.ItemWithDateDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ItemWithDateMapper {
    public static ItemWithDateDto toDtoWithDate(Item item, LocalDateTime lastBooking, LocalDateTime nextBooking) {
        List<CommentDto> comments = item.getComments().stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        return new ItemWithDateDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                nextBooking,
                comments
        );
    }
}