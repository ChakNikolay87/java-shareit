package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.model.ItemUpdatingRequest;

import java.util.*;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long id = 1;

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        Item item = ItemMapper.itemDtoToItem(itemDto);
        item.setOwnerId(userId);
        item.setId(getId());
        items.put(item.getId(), item);
        return ItemMapper.itemToItemDto(item);
    }

    @Override
    public ItemDto get(long itemId) {
        isItemExist(itemId);
        return ItemMapper.itemToItemDto(items.get(itemId));
    }

    @Override
    public List<ItemDto> findByOwnerId(long userId) {
        List<ItemDto> itemList = new ArrayList<>();
        items.forEach((key, value) -> {
            if (value.getOwnerId() == userId) {
                itemList.add(ItemMapper.itemToItemDto(value));
            }
        });
        return Collections.unmodifiableList(itemList);
    }

    @Override
    public List<ItemDto> getAll() {
        List<ItemDto> itemList = new ArrayList<>();
        items.forEach((key, value) -> itemList.add(ItemMapper.itemToItemDto(value)));
        return Collections.unmodifiableList(itemList);
    }

    @Override
    public ItemDto addUpdating(long itemId, Item item) {
        item.setId(itemId);
        items.put(itemId, item);
        return ItemMapper.itemToItemDto(item);
    }

    @Override
    public Item update(ItemUpdatingRequest itemUpdatingRequest) {
        long itemId = itemUpdatingRequest.getItemId();
        long userId = itemUpdatingRequest.getUserId();

        isItemExist(itemId);
        checkOwner(userId, itemId);

        Item item = items.get(itemId);

        if (itemUpdatingRequest.getName() == null && itemUpdatingRequest.getDescription() == null && itemUpdatingRequest.getAvailable() == null) {
            throw new ValidationException("At least one field for update should be provided.");
        }

        Optional.ofNullable(itemUpdatingRequest.getName())
                .filter(n -> !n.isBlank())
                .ifPresent(item::setName);

        Optional.ofNullable(itemUpdatingRequest.getDescription())
                .filter(d -> !d.isBlank())
                .ifPresent(item::setDescription);

        Optional.ofNullable(itemUpdatingRequest.getAvailable())
                .ifPresent(item::setAvailable);

        return item;
    }

    @Override
    public boolean remove(long itemId) {
        return items.remove(itemId) != null;
    }

    @Override
    public List<ItemDto> search(String searchText) {
        List<ItemDto> itemList = new ArrayList<>();
        String searchTextLower = searchText.toLowerCase();

        items.forEach((key, value) -> {
            if (value.getAvailable() &&
                    (value.getName().toLowerCase().contains(searchTextLower) || value.getDescription().toLowerCase().contains(searchTextLower))) {
                itemList.add(ItemMapper.itemToItemDto(value));
            }
        });

        return itemList;
    }

    private long getId() {
        return id++;
    }

    private void isItemExist(long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Item not found with ID: " + itemId);
        }
    }

    private void checkOwner(long userId, long itemId) {
        Item item = items.get(itemId);
        if (item == null || item.getOwnerId() != userId) {
            throw new ValidationException("You do not have permission to update this item.");
        }
    }
}
