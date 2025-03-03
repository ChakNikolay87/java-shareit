package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.util.HeaderConstants.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> createItemRequest(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                            @RequestBody ItemRequestDto itemRequestDto) {
        return new ResponseEntity<>(itemRequestService.createItemRequest(userId, itemRequestDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getItemRequests(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        return new ResponseEntity<>(itemRequestService.getItemRequestsByUser(userId), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllItemRequests(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        return new ResponseEntity<>(itemRequestService.getAllItemRequests(userId), HttpStatus.OK);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getItemRequestById(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                             @PathVariable Long requestId) {
        return new ResponseEntity<>(itemRequestService.getItemRequestById(userId, requestId), HttpStatus.OK);
    }
}