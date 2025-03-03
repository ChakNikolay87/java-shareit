package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getItemRequestsByUser(Long userId);

    List<ItemRequestDto> getAllItemRequests(Long userId);

    ItemRequestDto getItemRequestById(Long userId, Long requestId);
}