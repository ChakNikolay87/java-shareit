package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        Long requestId = item.getRequest() != null ? item.getRequest().getId() : null;
        List<CommentDto> comments = item.getComments().stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                requestId,
                comments

        );
    }

    public static Item toItem(ItemDto itemDto, User owner, ItemRequest itemRequest) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                itemRequest,
                List.of()

        );
    }
}