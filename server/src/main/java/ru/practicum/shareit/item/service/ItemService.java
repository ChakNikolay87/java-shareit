package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithDateDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    ItemWithDateDto getItemById(Long userId, Long itemId);

    List<ItemWithDateDto> getItemsByOwner(Long userId);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) throws ItemNotAvailableException;
}