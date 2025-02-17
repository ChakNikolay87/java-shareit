package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemUpdatingRequest;

import java.util.List;

import static ru.practicum.shareit.constants.HeaderConstants.X_SHARER_USER_ID;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto create(@RequestHeader(X_SHARER_USER_ID) long userId, @RequestBody @Valid ItemDto itemDto) {
        return service.create(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@RequestHeader(X_SHARER_USER_ID) long userId, @PathVariable long itemId) {
        return service.get(userId, itemId);
    }

    @GetMapping("/all")
    public List<ItemDto> getAll() {
        return service.getAll();
    }

    @GetMapping
    public List<ItemDto> getAllByUserId(@RequestHeader(X_SHARER_USER_ID) long userId) {
        return service.getAllByUserId(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(X_SHARER_USER_ID) long userId,
                          @PathVariable long itemId,
                          @RequestBody ItemUpdatingRequest itemUpdatingRequest) {
        itemUpdatingRequest.setItemId(itemId);
        itemUpdatingRequest.setUserId(userId);

        Item item = service.prepareUpdating(userId, itemId, itemUpdatingRequest);

        return service.update(userId, itemId, itemUpdatingRequest);
    }

    @DeleteMapping("/{itemId}")
    public void remove(@RequestHeader(X_SHARER_USER_ID) long userId, @PathVariable long itemId) {
        service.remove(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return service.search(text);
    }
}
