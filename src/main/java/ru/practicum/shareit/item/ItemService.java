package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemUpdatingRequest;

import java.util.List;

public interface ItemService {
    ItemDto create(long userId, ItemDto itemDto);

    ItemDto get(long userId, long itemId);

    List<ItemDto> getAll();

    List<ItemDto> getAllByUserId(long userId);

    ItemDto update(long userId, long itemId, ItemUpdatingRequest itemUpdatingRequest);

    Item prepareUpdating(long userId, long itemId, ItemUpdatingRequest itemUpdatingRequest);

    void remove(long userId, long itemId);

    List<ItemDto> search(String searchText);
}