package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemUpdatingRequest;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        log.info("Создание предмета: {}", itemDto);
        userRepository.getUser(userId);
        return itemRepository.create(itemDto, userId);
    }

    @Override
    public ItemDto get(long userId, long itemId) {
        userRepository.getUser(userId);
        log.info("Получение предмета по Id: {}", itemId);
        return itemRepository.get(itemId);
    }

    @Override
    public List<ItemDto> getAll() {
        return itemRepository.getAll();
    }

    @Override
    public List<ItemDto> getAllByUserId(long userId) {
        userRepository.getUser(userId);
        log.info("Получение всех предметов пользователя: {}", userId);
        return itemRepository.findByOwnerId(userId);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemUpdatingRequest itemUpdatingRequest) {
        log.info("Обновление предмета с запросом: {}", itemUpdatingRequest);
        Item updatedItem = itemRepository.update(itemUpdatingRequest);
        return itemRepository.addUpdating(itemId, updatedItem);
    }

    @Override
    public void remove(long userId, long itemId) {
        userRepository.getUser(userId);
        log.info("Удаление предмета: {}", itemId);
        itemRepository.remove(itemId);
    }

    @Override
    public List<ItemDto> search(String searchText) {
        log.info("Поиск предметов, содержащих: {}", searchText);
        if (searchText.isBlank()) {
            return new ArrayList<>();
        } else {
            return itemRepository.search(searchText);
        }
    }

    @Override
    public Item prepareUpdating(long userId, long itemId, ItemUpdatingRequest itemUpdatingRequest) {
        log.info("Подготовка предмета для обновления: {}", itemId);
        userRepository.getUser(userId);
        return itemRepository.update(itemUpdatingRequest);
    }
}
