package ru.practicum.shareit.item;

<<<<<<< HEAD
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
}
=======
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.ItemValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithDateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Valid @RequestBody ItemDto itemDto) throws ItemValidationException {
        return new ResponseEntity<>(itemService.createItem(userId, itemDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId,
                                              @RequestBody ItemDto itemDto) {
        return new ResponseEntity<>(itemService.updateItem(userId, itemId, itemDto), HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemWithDateDto> getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PathVariable Long itemId) {
        return new ResponseEntity<>(itemService.getItemById(userId, itemId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemWithDateDto>> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return new ResponseEntity<>(itemService.getItemsByOwner(userId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam(name = "text") String text) {
        return new ResponseEntity<>(itemService.searchItems(text), HttpStatus.OK);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long itemId,
                                                 @RequestBody CommentDto commentDto) throws ItemNotAvailableException {
        return new ResponseEntity<>(itemService.addComment(userId, itemId, commentDto), HttpStatus.CREATED);
    }
}
>>>>>>> 2acc074 (feat: Выполнил техническое задание 15 спринта)
