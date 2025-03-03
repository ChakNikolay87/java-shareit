package ru.practicum.shareit.request.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;


    @Override
    @Transactional
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User requestor = userService.getUserEntityById(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requestor);

        if (itemRequest.getItems() == null) {
            itemRequest.setItems(new ArrayList<>());
        }

        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDto(savedItemRequest);
    }

    @Override
    public List<ItemRequestDto> getItemRequestsByUser(Long userId) {
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId);
        return itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId) {
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId);
        return itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequestById(Long userId, Long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Запрос не найден"));
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }
}