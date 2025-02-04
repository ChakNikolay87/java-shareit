package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemUpdatingRequest;

import java.util.List;

public interface ItemRepository {
    Item create(Item item, long userId);

    ItemDto get(long itemId);

    List<ItemDto> findByOwnerId(long userId);

    List<ItemDto> getAll();

    ItemDto addUpdating(long itemId, Item item);

    Item update(ItemUpdatingRequest itemUpdatingRequest);

    boolean remove(long itemId);

    List<ItemDto> search(String searchText);
}
